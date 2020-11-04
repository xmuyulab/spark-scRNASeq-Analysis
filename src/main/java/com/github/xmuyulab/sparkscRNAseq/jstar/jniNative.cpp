#include "jni.h"
#include "com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarAlign.h"
#include "com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarInit.h"
#include "GetFastq.h"
#include <string>
#include <iostream>
#include <fstream>
#include <vector>
#include <sys/types.h>
#include <sys/stat.h>

#include "IncludeDefine.h"
#include "Parameters.h"
#include "SequenceFuns.h"
#include "Genome.h"
#include "Chain.h"
#include "ReadAlignChunk.h"
#include "ReadAlign.h"
#include "Stats.h"
#include "genomeGenerate.h"
#include "outputSJ.h"
#include "ThreadControl.h"
#include "GlobalVariables.h"
#include "TimeFunctions.h"
#include "ErrorWarning.h"
#include "sysRemoveDir.h"
#include "BAMfunctions.h"
#include "bamSortByCoordinate.h"
#include "Transcriptome.h"
#include "signalFromBAM.h"
#include "mapThreadsSpawn.h"
#include "ErrorWarning.h"
#include "SjdbClass.h"
#include "sjdbInsertJunctions.h"
#include "Variation.h"
#include "Solo.h"
#include "samHeaders.h"

#include "twoPassRunPass1.h"

#include "htslib/htslib/sam.h"
//#include "parametersDefault.xxd"

/*
 * Class:     com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarInit
 * Method:    getAddress
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarInit_getAddress
    (JNIEnv * env, jobject) {
      // 获取GetFastq地址
      jlong cAddress = (jlong)new GetFastq();
      return cAddress;
    }

/*
 * Class:     com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarAlign
 * Method:    tranFastq
 * Signature: (J[Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarAlign_tranFastq
      (JNIEnv * env, jobject, jlong cAddress, jobjectArray fastqLine) {
        // 生成指向GetFastq的指针
        GetFastq* cPoint = (GetFastq*)cAddress;
        // 获取fastqLine的数组长度
        jsize fSize = env->GetArrayLength(fastqLine);
        for (int i = 0; i < fSize; i++) {
          jstring chardata = (jstring)env->GetObjectArrayElement(fastqLine, i);
          std::string tmp = (std::string)env->GetStringUTFChars(chardata, NULL);
          cPoint->tranString(tmp);
        }
      }
  
/*
 * Class:     com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarAlign
 * Method:    runStar
 * Signature: (J[[C)Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarAlign_runStar
    (JNIEnv * env, jobject, jlong cAddress, jobjectArray commandLine) {
      //  恢复命令行
      jint i, j, col;
      jarray tmpArray;
      jchar* coldata;
      jint row = env->GetArrayLength(commandLine);
      char** argIn = new char*[row];
      for (i = 0; i < row; i++) {
        tmpArray = (jarray)env->GetObjectArrayElement(commandLine, i);
        col = env->GetArrayLength(tmpArray);
        coldata = env->GetCharArrayElements((jcharArray)tmpArray, 0);
        argIn[i] = new char[col];
        for(j = 0; j < col; j++) {
          argIn[i][j] = coldata[j];
        }
      }
      //  开始运行STAR
      time(&g_statsAll.timeStart);
      Parameters P;
      P.inputParameters(11, argIn);
      //P.inOut->readIn[0].open("/home/liuyu/fastq", std::ios::app);
      P.inOut->readIn[0] << ((GetFastq*)cAddress)->tmpReadIn.rdbuf();
      //P.inOut->readIn[0].open("/home/liuyu/fastq", std::ios::app);
      //std::cout << ((GetFastq*)cAddress)->tmpReadIn.rdbuf();
     P.inOut->logMain << timeMonthDayTime(g_statsAll.timeStart) << "...started STAR run\n" << flush;
     P.inOut->logMain << timeMonthDayTime(g_statsAll.timeStart) << "...Hello World!\n" << flush;
    //generate genome
    if (P.runMode=="alignReads") {
        //continue
    } else if (P.runMode=="genomeGenerate") {
        Genome genomeMain(P, P.pGe);
        genomeMain.genomeGenerate();
        (void) sysRemoveDir (P.outFileTmp);
        P.inOut->logMain << "DONE: Genome generation, EXITING\n" << flush;
        exit(0);
    } else if (P.runMode=="liftOver") {
        for (uint ii=0; ii<P.pGe.gChainFiles.size();ii++) {
            Chain chain(P,P.pGe.gChainFiles.at(ii));
            chain.liftOverGTF(P.pGe.sjdbGTFfile,P.outFileNamePrefix+"GTFliftOver_"+to_string(ii+1)+".gtf");
            P.inOut->logMain << "DONE: lift-over of GTF file, EXITING\n" << flush;
            exit(0);
        };
    } else {
        P.inOut->logMain << "EXITING because of INPUT ERROR: unknown value of input parameter runMode=" <<P.runMode<<endl<<flush;
        exit(1);
    };

    ////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// Genome
    Genome genomeMain (P, P.pGe);
    genomeMain.genomeLoad();
   
    //calculate genome-related parameters
    Transcriptome *transcriptomeMain=NULL;
    genomeMain.Var=new Variation(P, genomeMain.chrStart, genomeMain.chrNameIndex);

    SjdbClass sjdbLoci;

    if (P.sjdbInsert.pass1) {
        Genome genomeMain1=genomeMain;//not sure if I need to create the copy - genomeMain1 below should not be changed
        sjdbInsertJunctions(P, genomeMain, genomeMain1, sjdbLoci);
    };

/////////////////////////////////////////////////////////////////////////////////////////////////START
    if (P.runThreadN>1) {
        g_threadChunks.threadArray=new pthread_t[P.runThreadN];
        pthread_mutex_init(&g_threadChunks.mutexInRead, NULL);
        pthread_mutex_init(&g_threadChunks.mutexOutSAM, NULL);
        pthread_mutex_init(&g_threadChunks.mutexOutBAM1, NULL);
        pthread_mutex_init(&g_threadChunks.mutexOutUnmappedFastx, NULL);
        pthread_mutex_init(&g_threadChunks.mutexOutFilterBySJout, NULL);
        pthread_mutex_init(&g_threadChunks.mutexStats, NULL);
        pthread_mutex_init(&g_threadChunks.mutexBAMsortBins, NULL);
        pthread_mutex_init(&g_threadChunks.mutexError, NULL);
    };

    g_statsAll.progressReportHeader(P.inOut->logProgress);

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// 2-pass 1st pass
    twoPassRunPass1(P, genomeMain, transcriptomeMain, sjdbLoci);

    if ( P.quant.yes ) {//load transcriptome
        transcriptomeMain=new Transcriptome(P);
    };

    //initialize Stats
    g_statsAll.resetN();
    time(&g_statsAll.timeStartMap);
    *P.inOut->logStdOut <<"##########################################################\n" <<
    "readFilesCommandString = " << P.readFilesCommandString << "\n" <<
    "readFilesPrefixFinal = " << P.readFilesPrefixFinal << "\n" <<
    "readFilesIn.at(0) = " << P.readFilesIn.at(0) << "\n" <<
    "readFilesIn.size() = " << P.readFilesIn.size() << "\n" <<
    "#######################################################################\n" << flush;
    *P.inOut->logStdOut << timeMonthDayTime(g_statsAll.timeStartMap) << " ..... started mapping\n" <<flush;
    g_statsAll.timeLastReport=g_statsAll.timeStartMap;

    //SAM headers
    samHeaders(P, *genomeMain.genomeOut.g, *transcriptomeMain);

    //initialize chimeric parameters here - note that chimeric parameters require samHeader
    P.pCh.initialize(&P);
    
    // this does not seem to work at the moment
    // P.inOut->logMain << "mlock value="<<mlockall(MCL_CURRENT|MCL_FUTURE) <<"\n"<<flush;

    // prepare chunks and spawn mapping threads
    ReadAlignChunk *RAchunk[P.runThreadN];
    for (int ii=0;ii<P.runThreadN;ii++) {
        RAchunk[ii]=new ReadAlignChunk(P, genomeMain, transcriptomeMain, ii);
    };

    if (P.runRestart.type!=1)
        mapThreadsSpawn(P, RAchunk);

    if (P.outFilterBySJoutStage==1) {//completed stage 1, go to stage 2
        P.inOut->logMain << "Completed stage 1 mapping of outFilterBySJout mapping\n"<<flush;
        outputSJ(RAchunk,P);//collapse novel junctions
        P.readFilesIndex=-1;

        P.outFilterBySJoutStage=2;
        if (P.outBAMcoord) {
            for (int it=0; it<P.runThreadN; it++) {//prepare the unmapped bin
                RAchunk[it]->chunkOutBAMcoord->coordUnmappedPrepareBySJout();
            };
        };

        mapThreadsSpawn(P, RAchunk);
    };

    //close some BAM files
    if (P.inOut->outBAMfileUnsorted!=NULL) {
        bgzf_flush(P.inOut->outBAMfileUnsorted);
        bgzf_close(P.inOut->outBAMfileUnsorted);
    };
    if (P.inOut->outQuantBAMfile!=NULL) {
        bgzf_flush(P.inOut->outQuantBAMfile);
        bgzf_close(P.inOut->outQuantBAMfile);
    };

    if (P.outBAMcoord && P.limitBAMsortRAM==0) {//make it equal ot the genome size
        P.limitBAMsortRAM=genomeMain.nGenome+genomeMain.SA.lengthByte+genomeMain.SAi.lengthByte;
    };

    time(&g_statsAll.timeFinishMap);
    //std::string oss = P.inOut->outSAMString.str();
    //char* cs = new char[oss.length()+1];
    //strcpy(cs, oss.c_str());
    // long oi;
    // std::string tmp = "", temp = "";
    // std::vector<std::string> vc;
    // long countTimes = 0;
    // for (oi = 1; getline(P.inOut->outSAMString, tmp, '\n'); oi++) {
    //     temp += (tmp+'\n');
    //     countTimes++;
    //     if (oi%32786 == 0) {
    //         vc.push_back(temp);
    //         temp = "";
    //         oi = 0;
    //     }
    // }

    //std::cout << P.inOut->outSAMString.rdbuf();
    *P.inOut->logStdOut << timeMonthDayTime(g_statsAll.timeFinishMap) << " ..... finished mapping\n" <<flush;

    //no need for genome anymore, free the memory
    genomeMain.freeMemory();

    //aggregate output junctions
    //collapse splice junctions from different threads/chunks, and output them
    if (P.runRestart.type!=1)
        outputSJ(RAchunk,P);

    //solo counts
    Solo soloMain(RAchunk,P,*RAchunk[0]->chunkTr);
    soloMain.processAndOutput();

    if ( P.quant.geCount.yes ) {//output gene quantifications
        for (int ichunk=1; ichunk<P.runThreadN; ichunk++) {//sum counts from all chunks into 0th chunk
            RAchunk[0]->chunkTr->quants->addQuants(*(RAchunk[ichunk]->chunkTr->quants));
        };
        RAchunk[0]->chunkTr->quantsOutput();
    };

    if (P.runThreadN>1 && P.outSAMorder=="PairedKeepInputOrder") {//concatenate Aligned.* files
        RAchunk[0]->chunkFilesCat(P.inOut->outSAM, P.outFileTmp + "/Aligned.out.sam.chunk", g_threadChunks.chunkOutN);
    };

    bamSortByCoordinate(P, RAchunk, genomeMain, soloMain);
    
    //wiggle output
    if (P.outWigFlags.yes) {
        *(P.inOut->logStdOut) << timeMonthDayTime() << " ..... started wiggle output\n" <<flush;
        P.inOut->logMain << timeMonthDayTime() << " ..... started wiggle output\n" <<flush;
        string wigOutFileNamePrefix=P.outFileNamePrefix + "Signal";
        signalFromBAM(P.outBAMfileCoordName, wigOutFileNamePrefix, P);
    };

    g_statsAll.writeLines(P.inOut->outChimJunction, P.pCh.outJunctionFormat, "#", STAR_VERSION + string("   ") + P.commandLine);

    g_statsAll.progressReport(P.inOut->logProgress);
    P.inOut->logProgress  << "ALL DONE!\n"<<flush;
    P.inOut->logFinal.open((P.outFileNamePrefix + "Log.final.out").c_str());
    g_statsAll.reportFinal(P.inOut->logFinal);
    *P.inOut->logStdOut << timeMonthDayTime(g_statsAll.timeFinish) << " ..... finished successfully\n" <<flush;

    P.inOut->logMain  << "ALL DONE!\n" << flush;
    if (P.outTmpKeep=="None") {
        sysRemoveDir (P.outFileTmp);
    };

    P.closeReadsFiles();//this will kill the readFilesCommand processes if necessary
    //genomeMain.~Genome(); //need explicit call because of the 'delete P.inOut' below, which will destroy P.inOut->logStdOut
    if (genomeMain.sharedMemory != NULL) {//need explicit call because this destructor will write to files which are deleted by 'delete P.inOut' below
        delete genomeMain.sharedMemory;
        genomeMain.sharedMemory = NULL;
    };
    P.inOut->logMain << "..................Here we run all content of STAR..................\n";
    P.inOut->logMain << "*************************************************" << P.inOut->vOutSam.size();
    long vSize = P.inOut->vOutSam.size();
    jobjectArray res = (jobjectArray)env->NewObjectArray(vSize, env->FindClass("java/lang/String"), env->NewStringUTF(""));
    for (long oi = 0; oi < vSize; oi++) {
        env->SetObjectArrayElement(res, oi, env->NewStringUTF(((char*)P.inOut->vOutSam[oi].c_str())));
    }
    delete P.inOut; //to close files

    return res;
}