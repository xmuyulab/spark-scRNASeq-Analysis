#include <sys/types.h>
#include <sys/stat.h>
#include "jni.h"
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
#include <iostream>

#include "htslib/htslib/sam.h"
#include "com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarInit.h"

/*
 * Class:     com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarInit
 * Method:    openParameters
 * Signature: (I[[C)[J
 */
JNIEXPORT jlongArray JNICALL Java_com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarInit_openParameters
  (JNIEnv * env, jclass, jint argInN, jobjectArray tmpargIn) {
    jint i,j,col;
    jarray tmpArray;
    jchar* coldata;
    jint row = env->GetArrayLength(tmpargIn);
    char** argIn = new char*[row];
    for(i = 0; i < row; i++){
      tmpArray = (jarray)env->GetObjectArrayElement(tmpargIn, i);
      col = env->GetArrayLength(tmpArray);
      coldata = env->GetCharArrayElements((jcharArray)tmpArray, 0);
      argIn[i] = new char[col];
      for(j = 0; j < col; j++){
        argIn[i][j] = coldata[j];
      }
    }

    // STAR begin here
    time(&g_statsAll.timeStart);
    printf("time(&g_statsAll.timeStart); is ok \n");
    
    Parameters* P = new Parameters;           //all parameters
    P->inputParameters(argInN, argIn);
    Genome* mainGenome = new Genome(*P);
    mainGenome->genomeLoad();
    Transcriptome *mainTranscriptome=NULL;
    mainGenome->Var=new Variation(*P, (*mainGenome).chrStart, (*mainGenome).chrNameIndex);
    SjdbClass* sjdbLoci;
    jlong tmpresult[4];
    jlongArray result;
    result = env->NewLongArray(4);
    tmpresult[0] = (jlong) P;
    tmpresult[1] = (jlong) mainGenome;
    tmpresult[2] = (jlong) mainTranscriptome;
    tmpresult[3] = (jlong) sjdbLoci;
    env->SetLongArrayRegion(result, 0, 3, tmpresult);
    return result;
  }

/*
 * Class:     com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarInit
 * Method:    openStar
 * Signature: (JJJJ)V
 */
JNIEXPORT void JNICALL Java_com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarInit_openStar
  (JNIEnv *, jobject, jlong ParametersAddress, jlong GenomeAddress, jlong TranscriptomeAddress, jlong SjdbAddress) {
    Parameters* P;
    P = (Parameters*)ParametersAddress;
    Genome* mainGenome = (Genome*)GenomeAddress;
    Transcriptome *mainTranscriptome;
    mainTranscriptome = (Transcriptome*)TranscriptomeAddress;
    SjdbClass* sjdbLoci = (SjdbClass*)SjdbAddress;
// /////////////////////////////////////////////////////////////////////////////////////////////////START
  if (P->runThreadN>1) {
    g_threadChunks.threadArray=new pthread_t[P->runThreadN];
    pthread_mutex_init(&g_threadChunks.mutexInRead, NULL);
    pthread_mutex_init(&g_threadChunks.mutexOutSAM, NULL);
    pthread_mutex_init(&g_threadChunks.mutexOutBAM1, NULL);
    pthread_mutex_init(&g_threadChunks.mutexOutUnmappedFastx, NULL);
    pthread_mutex_init(&g_threadChunks.mutexOutFilterBySJout, NULL);
    pthread_mutex_init(&g_threadChunks.mutexStats, NULL);
    pthread_mutex_init(&g_threadChunks.mutexBAMsortBins, NULL);
    pthread_mutex_init(&g_threadChunks.mutexError, NULL);
  };

  g_statsAll.progressReportHeader(P->inOut->logProgress);

  //initialize Stats
  g_statsAll.resetN();
  time(&g_statsAll.timeStartMap);
  *P->inOut->logStdOut << timeMonthDayTime(g_statsAll.timeStartMap) << " ..... started mapping\n" <<flush;

  g_statsAll.timeLastReport=g_statsAll.timeStartMap;

  if (P->outSAMmode != "None") {//open SAM file and write header
    ostringstream samHeaderStream;
    
    for (uint ii=0;ii<mainGenome->nChrReal;ii++) {
      samHeaderStream << "@SQ\tSN:"<< mainGenome->chrName.at(ii) <<"\tLN:"<<mainGenome->chrLength[ii]<<"\n";
    };
    mainGenome->chrNameAll=mainGenome->chrName;
    mainGenome->chrLengthAll=mainGenome->chrLength;
    {//add exra references
      ifstream extrastream (P->pGe.gDir + "/extraReferences.txt");
      while (extrastream.good()) {
        string line1;
        getline(extrastream,line1);
        istringstream stream1 (line1);
        string field1;
        stream1 >> field1;//should check for @SQ
        if (field1!="") {//skip blank lines
          samHeaderStream << line1 <<"\n";

          stream1 >> field1;
          mainGenome->chrNameAll.push_back(field1.substr(3));
          stream1 >> field1;
          mainGenome->chrLengthAll.push_back((uint) stoll(field1.substr(3)));
        };
      };
      extrastream.close();
    };
    if (P->outSAMheaderPG.at(0)!="-") {
      samHeaderStream << P->outSAMheaderPG.at(0);
      for (uint ii=1;ii<P->outSAMheaderPG.size(); ii++) {
        samHeaderStream << "\t" << P->outSAMheaderPG.at(ii);
      };
      samHeaderStream << "\n";
    };

    samHeaderStream << "@PG\tID:STAR\tPN:STAR\tVN:" << STAR_VERSION <<"\tCL:" << P->commandLineFull <<"\n";

    if (P->outSAMheaderCommentFile!="-") {
      printf("P->outSAMheaderCommentFile!=\"-\"\n");
      ifstream comstream (P->outSAMheaderCommentFile);
      while (comstream.good()) {
        string line1;
        getline(comstream,line1);
        if (line1.find_first_not_of(" \t\n\v\f\r")!=std::string::npos) {//skip blank lines
          samHeaderStream << line1 <<"\n";
        };
      };
      comstream.close();
    };


    for (uint32 ii=0;ii<P->outSAMattrRGlineSplit.size();ii++) {//@RG lines
      samHeaderStream << "@RG\t" << P->outSAMattrRGlineSplit.at(ii) <<"\n";
    };


    samHeaderStream <<  "@CO\t" <<"user command line: " << P->commandLine <<"\n";

    samHeaderStream << P->samHeaderExtra;

    if (P->outSAMheaderHD.at(0)!="-") {
      printf("P.outSAMheaderHD.at(0)!=\"-\"\n");
      P->samHeaderHD = P->outSAMheaderHD.at(0);
      for (uint ii=1;ii<P->outSAMheaderHD.size(); ii++) {
        P->samHeaderHD +="\t" + P->outSAMheaderHD.at(ii);
      };
    } else {
      printf("P.outSAMheaderHD.at(0)==\"-\"\n");
      P->samHeaderHD = "@HD\tVN:1.4";
    };


    P->samHeader=P->samHeaderHD+"\n"+samHeaderStream.str();
// //         //for the sorted BAM, need to add SO:cooridnate to the header line
    P->samHeaderSortedCoord=P->samHeaderHD + (P->outSAMheaderHD.size()==0 ? "" : "\tSO:coordinate") + "\n" + samHeaderStream.str();

    if (P->outSAMbool) {//
      *(P->inOut)->outSAM << P->samHeader;
    };
    if (P->outBAMunsorted){
      outBAMwriteHeader(P->inOut->outBAMfileUnsorted, P->samHeader, mainGenome->chrNameAll, mainGenome->chrLengthAll);
    };

    if ( P->quant.trSAM.bamYes ) {
      samHeaderStream.str("");
      vector <uint> trlength;
      for (uint32 ii=0;ii<mainTranscriptome->trID.size();ii++) {
        uint32 iex1=mainTranscriptome->trExI[ii]+mainTranscriptome->trExN[ii]-1; //last exon of the transcript
        trlength.push_back(mainTranscriptome->exLenCum[iex1]+mainTranscriptome->exSE[2*iex1+1]-mainTranscriptome->exSE[2*iex1]+1);
        samHeaderStream << "@SQ\tSN:"<< mainTranscriptome->trID.at(ii) <<"\tLN:"<<trlength.back()<<"\n";
      };
    for (uint32 ii=0;ii<P->outSAMattrRGlineSplit.size();ii++) {//@RG lines
      samHeaderStream << "@RG\t" << P->outSAMattrRGlineSplit.at(ii) <<"\n";
    };
    outBAMwriteHeader(P->inOut->outQuantBAMfile,samHeaderStream.str(),mainTranscriptome->trID,trlength);
  };

};

// //     //initialize chimeric parameters here - note that chimeric parameters require samHeader
    P->pCh.initialize(P);
// //     // prepare chunks and spawn mapping threads
    ReadAlignChunk *RAchunk[P->runThreadN];
    for (int ii=0;ii<P->runThreadN;ii++) {
      RAchunk[ii]=new ReadAlignChunk(*P, *mainGenome, mainTranscriptome, ii);
    };
    
    mapThreadsSpawn(*P, RAchunk);

// //     if (P.outFilterBySJoutStage==1) {//completed stage 1, go to stage 2
// //         P.inOut->logMain << "Completed stage 1 mapping of outFilterBySJout mapping\n"<<flush;
// //         outputSJ(RAchunk,P);//collapse novel junctions
// //         P.readFilesIndex=-1;

// //         P.outFilterBySJoutStage=2;
// //         if (P.outBAMcoord) {
// //             for (int it=0; it<P.runThreadN; it++) {//prepare the unmapped bin
// //                 RAchunk[it]->chunkOutBAMcoord->coordUnmappedPrepareBySJout();
// //             };
// //         };

// //         mapThreadsSpawn(P, RAchunk);
// //     };

// //     //close some BAM files
// //     if (P.inOut->outBAMfileUnsorted!=NULL) {
// //         bgzf_flush(P.inOut->outBAMfileUnsorted);
// //         bgzf_close(P.inOut->outBAMfileUnsorted);
// //     };
// //     if (P.inOut->outQuantBAMfile!=NULL) {
// //         bgzf_flush(P.inOut->outQuantBAMfile);
// //         bgzf_close(P.inOut->outQuantBAMfile);
// //     };

// //     if (P.outBAMcoord && P.limitBAMsortRAM==0) {//make it equal ot the genome size
// //         P.limitBAMsortRAM=mainGenome.nGenome+mainGenome.SA.lengthByte+mainGenome.SAi.lengthByte;
// //     };

// //     time(&g_statsAll.timeFinishMap);
// //     *P.inOut->logStdOut << timeMonthDayTime(g_statsAll.timeFinishMap) << " ..... finished mapping\n" <<flush;

// //     //no need for genome anymore, free the memory
// //     mainGenome.freeMemory();

// //     //aggregate output junctions
// //     //collapse splice junctions from different threads/chunks, and output them
// //     outputSJ(RAchunk,P);

// //     //solo counts
// //     Solo soloMain(RAchunk,P,*RAchunk[0]->chunkTr);
// //     soloMain.processAndOutput();

// //     if ( P.quant.geCount.yes ) {//output gene quantifications
// //         for (int ichunk=1; ichunk<P.runThreadN; ichunk++) {//sum counts from all chunks into 0th chunk
// //             RAchunk[0]->chunkTr->quants->addQuants(*(RAchunk[ichunk]->chunkTr->quants));
// //         };
// //         RAchunk[0]->chunkTr->quantsOutput();
// //     };

// //     if (P.runThreadN>1 && P.outSAMorder=="PairedKeepInputOrder") {//concatenate Aligned.* files
// //         RAchunk[0]->chunkFilesCat(P.inOut->outSAM, P.outFileTmp + "/Aligned.out.sam.chunk", g_threadChunks.chunkOutN);
// //     };

// //     bamSortByCoordinate(P, RAchunk, mainGenome, soloMain);
    
// //     //wiggle output
// //     if (P.outWigFlags.yes) {
// //         *(P.inOut->logStdOut) << timeMonthDayTime() << " ..... started wiggle output\n" <<flush;
// //         P.inOut->logMain << timeMonthDayTime() << " ..... started wiggle output\n" <<flush;
// //         string wigOutFileNamePrefix=P.outFileNamePrefix + "Signal";
// //         signalFromBAM(P.outBAMfileCoordName, wigOutFileNamePrefix, P);
// //     };

// //     g_statsAll.writeLines(P.inOut->outChimJunction, P.pCh.outJunctionFormat, "#", STAR_VERSION + string("   ") + P.commandLine);

// //     g_statsAll.progressReport(P.inOut->logProgress);
// //     P.inOut->logProgress  << "ALL DONE!\n"<<flush;
// //     P.inOut->logFinal.open((P.outFileNamePrefix + "Log.final.out").c_str());
//     g_statsAll.reportFinal(P.inOut->logFinal);
//     *P.inOut->logStdOut << timeMonthDayTime(g_statsAll.timeFinish) << " ..... finished successfully\n" <<flush;

//     P.inOut->logMain  << "ALL DONE!\n" << flush;
//     if (P.outTmpKeep=="None") {
//         sysRemoveDir (P.outFileTmp);
//     };

//     P.closeReadsFiles();//this will kill the readFilesCommand processes if necessary
//     //mainGenome.~Genome(); //need explicit call because of the 'delete P.inOut' below, which will destroy P.inOut->logStdOut
//     if (mainGenome.sharedMemory != NULL) {//need explicit call because this destructor will write to files which are deleted by 'delete P.inOut' below
//         delete mainGenome.sharedMemory;
//         mainGenome.sharedMemory = NULL;
//     };

//     delete P.inOut; //to close files

    // return 0;
  }

