# user may define these whole flags
# LDFLAGS
# CPPFLAGS
# CXXFLAGS
# CFLAGS

# or these user-set flags that will be added to standard flags
LDFLAGSextra ?=
CXXFLAGSextra ?=

# user may define the compiler
CXX ?= g++

# pre-defined flags
LDFLAGS_shared := -pthread -Lhtslib -Bstatic -lhts -Bdynamic -lz
LDFLAGS_static := -static -static-libgcc -pthread -Lhtslib -lhts -lz
LDFLAGS_Mac :=-pthread -lz htslib/libhts.a
LDFLAGS_Mac_static :=-pthread -lz -static-libgcc htslib/libhts.a
LDFLAGS_gdb := $(LDFLAGS_shared)

COMPTIMEPLACE := -D'COMPILATION_TIME_PLACE="$(shell echo `date` $(HOSTNAME):`pwd`)"'

# Defaults, can be overridden by make arguments or environment
CXXFLAGS ?= -pipe -Wall -Wextra
CFLAGS ?= -fPIC -pipe -Wall -Wextra -O3

# Unconditionally set essential flags and optimization options
CXXFLAGS_common := -fPIC -std=c++11 -fopenmp $(COMPTIMEPLACE)
CXXFLAGS_main := -O3 $(CXXFLAGS_common)
CXXFLAGS_gdb := -O -g3 $(CXXFLAGS_common)

##########################################################################################################
OBJECTS = soloInputFeatureUMI.o SoloFeature_countSmartSeq.o SoloFeature_redistributeReadsByCB.o \
	SoloFeature_quantTranscript.o SoloFeature_sumThreads.o SoloFeature_countVelocyto.o SoloFeature_countCBgeneUMI.o \
	Transcriptome_classifyAlign.o SoloFeature_cellFiltering.o \
	SoloFeature_statsOutput.o bamSortByCoordinate.o SoloBarcode.o \
	ParametersSolo.o SoloRead.o SoloRead_record.o \
	SoloReadBarcode.o SoloReadBarcode_getCBandUMI.o SoloBarcode_extractBarcode.o \
	SoloReadFeature.o SoloReadFeature_record.o SoloReadFeature_inputRecords.o \
	Solo.o SoloFeature.o SoloFeature_collapseUMI.o SoloFeature_outputResults.o SoloFeature_processRecords.o SoloFeature_addBAMtags.o \
	ReadAlign_transformGenome.o Genome_transformGenome.o Transcript_convertGenomeCigar.o \
	twoPassRunPass1.o samHeaders.o Genome_genomeLoad.o Genome_genomeOutLoad.o Transcript_transformGenome.o ReadAlign_outputSpliceGraphSAM.o \
	ReadAlign_mapOneReadSpliceGraph.o SpliceGraph.o SpliceGraph_swScoreSpliced.o SpliceGraph_swTraceBack.o \
	SpliceGraph_findSuperTr.o sjAlignSplit.o \
	GTF.o GTF_transcriptGeneSJ.o GTF_superTranscript.o SuperTranscriptome.o \
	ReadAlign_outputAlignments.o  \
	ReadAlign.o STAR.o \
	SharedMemory.o PackedArray.o SuffixArrayFuns.o Parameters.o InOutStreams.o SequenceFuns.o Genome.o Stats.o \
	Transcript.o Transcript_alignScore.o Transcript_generateCigarP.o Chain.o \
	Transcript_variationAdjust.o Variation.o ReadAlign_waspMap.o \
	ReadAlign_storeAligns.o ReadAlign_stitchPieces.o ReadAlign_multMapSelect.o ReadAlign_mapOneRead.o readLoad.o \
	ReadAlignChunk.o ReadAlignChunk_processChunks.o ReadAlignChunk_mapChunk.o \
	OutSJ.o outputSJ.o blocksOverlap.o ThreadControl.o sysRemoveDir.o \
	ReadAlign_maxMappableLength2strands.o binarySearch2.o\
	ReadAlign_outputTranscriptSAM.o ReadAlign_outputTranscriptSJ.o ReadAlign_outputTranscriptCIGARp.o ReadAlign_calcCIGAR.cpp \
	ReadAlign_createExtendWindowsWithAlign.o ReadAlign_assignAlignToWindow.o ReadAlign_oneRead.o \
	ReadAlign_stitchWindowSeeds.o \
	ReadAlign_peOverlapMergeMap.o ReadAlign_mappedFilter.o \
	ParametersChimeric_initialize.o ReadAlign_chimericDetection.o ReadAlign_chimericDetectionOld.o ReadAlign_chimericDetectionOldOutput.o\
	ChimericDetection.o ChimericDetection_chimericDetectionMult.o ReadAlign_chimericDetectionPEmerged.o \
	ChimericSegment.cpp ChimericAlign.cpp ChimericAlign_chimericJunctionOutput.o ChimericAlign_chimericStitching.o \
	stitchWindowAligns.o extendAlign.o stitchAlignToTranscript.o \
	Genome_genomeGenerate.o genomeParametersWrite.o genomeScanFastaFiles.o genomeSAindex.o \
	Genome_insertSequences.o Genome_consensusSequence.o \
	insertSeqSA.o funCompareUintAndSuffixes.o funCompareUintAndSuffixesMemcmp.o \
	TimeFunctions.o ErrorWarning.o streamFuns.o stringSubstituteAll.o \
	Transcriptome.o Transcriptome_quantAlign.o Transcriptome_geneFullAlignOverlap.o \
	ReadAlign_quantTranscriptome.o Quantifications.o Transcriptome_geneCountsAddAlign.o \
	sjdbLoadFromFiles.o sjdbLoadFromStream.o sjdbPrepare.o sjdbBuildIndex.o sjdbInsertJunctions.o mapThreadsSpawn.o \
	Parameters_readFilesInit.o Parameters_openReadsFiles.cpp Parameters_closeReadsFiles.cpp Parameters_readSAMheader.o \
	bam_cat.o serviceFuns.o GlobalVariables.cpp \
	BAMoutput.o BAMfunctions.o ReadAlign_alignBAM.o BAMbinSortByCoordinate.o signalFromBAM.o bamRemoveDuplicates.o BAMbinSortUnmapped.o \
	GetFastq.o jniNative.o

LIBHTS_OBJS = \
	./htslib/kfunc.o \
	./htslib/knetfile.o \
	./htslib/kstring.o \
	./htslib/bgzf.o \
	./htslib/faidx.o \
	./htslib/hfile.o \
	./htslib/hfile_net.o \
	./htslib/hts.o \
	./htslib/sam.o \
	./htslib/synced_bcf_reader.o \
	./htslib/vcf_sweep.o \
	./htslib/tbx.o \
	./htslib/vcf.o \
	./htslib/vcfutils.o \
	./htslib/cram/cram_codecs.o \
	./htslib/cram/cram_decode.o \
	./htslib/cram/cram_encode.o \
	./htslib/cram/cram_index.o \
	./htslib/cram/cram_io.o \
	./htslib/cram/cram_samtools.o \
	./htslib/cram/cram_stats.o \
	./htslib/cram/files.o \
	./htslib/cram/mFILE.o \
	./htslib/cram/md5.o \
	./htslib/cram/open_trace_file.o \
	./htslib/cram/pooled_alloc.o \
	./htslib/cram/sam_header.o \
	./htslib/cram/string_alloc.o \
	./htslib/cram/thread_pool.o \
	./htslib/cram/vlen.o \
	./htslib/cram/zfio.o

SOURCES := $(wildcard *.cpp) $(wildcard *.c)


%.o : %.cpp
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $<

%.o : %.c
	$(CXX) -c $(CPPFLAGS) $(CFLAGS) $<

all: STAR libSTAR.so

.PHONY: clean
clean:
	rm -f *.o STAR STARstatic STARlong Depend.list *.so

.PHONY: clean_solo
clean_solo:
	rm -f Solo*.o

.PHONY: CLEAN
CLEAN:
	rm -f *.o STAR Depend.list
	$(MAKE) -C htslib clean

.PHONY: cleanRelease
cleanRelease:
	rm -f *.o Depend.list
	$(MAKE) -C htslib clean

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(MAKECMDGOALS),cleanRelease)
ifneq ($(MAKECMDGOALS),CLEAN)
ifneq ($(MAKECMDGOALS),STARforMac)
ifneq ($(MAKECMDGOALS),STARforMacGDB)
Depend.list: $(SOURCES) parametersDefault.xxd htslib
	echo $(SOURCES)
	'rm' -f ./Depend.list
	$(CXX) $(CXXFLAGS_common) -MM $^ >> Depend.list
include Depend.list
endif
endif
endif
endif
endif

htslib : htslib/libhts.a

htslib/libhts.a :
	$(MAKE) -C htslib lib-static

parametersDefault.xxd: parametersDefault
	xxd -i parametersDefault > parametersDefault.xxd

STAR : CXXFLAGS := $(CXXFLAGSextra) $(CXXFLAGS_main) $(CXXFLAGS)
STAR : LDFLAGS := $(LDFLAGSextra) $(LDFLAGS_shared) $(LDFLAGS)
STAR : Depend.list parametersDefault.xxd $(OBJECTS)
	$(CXX) -fPIC -o STAR $(CXXFLAGS) $(OBJECTS) $(LDFLAGS) -lz

libSTAR.so : Depend.list parametersDefault.xxd $(OBJECTS) $(LIBHTS_OBJS)
	$(CXX) -fPIC -std=c++11 -fopenmp -shared -o libSTAR.so $(OBJECTS) $(LIBHTS_OBJS) $(LDFLAGS) -lz

POSIXSHARED : CXXFLAGS := $(CXXFLAGSextra) $(CXXFLAGS_main) -DPOSIX_SHARED_MEM $(CXXFLAGS)
POSIXSHARED : LDFLAGS := $(LDFLAGSextra) $(LDFLAGS_shared) $(LDFLAGS)
POSIXSHARED : Depend.list parametersDefault.xxd $(OBJECTS)
	$(CXX) -o STAR $(CXXFLAGS) $(OBJECTS) $(LDFLAGS)

STARstatic : CXXFLAGS := $(CXXFLAGSextra) $(CXXFLAGS_main) $(CXXFLAGS)
STARstatic : LDFLAGS := $(LDFLAGSextra) $(LDFLAGS_static) $(LDFLAGS)
STARstatic : Depend.list parametersDefault.xxd $(OBJECTS)
	$(CXX) -o STAR $(CXXFLAGS) $(OBJECTS) $(LDFLAGS)

STARlong : CXXFLAGS := $(CXXFLAGSextra) $(CXXFLAGS_main) -D'COMPILE_FOR_LONG_READS' $(CXXFLAGS)
STARlong : LDFLAGS := $(LDFLAGSextra) $(LDFLAGS_shared) $(LDFLAGS)
STARlong : Depend.list parametersDefault.xxd $(OBJECTS)
	$(CXX) -o STARlong $(CXXFLAGS) $(OBJECTS) $(LDFLAGS)

STARlongStatic : CXXFLAGS := $(CXXFLAGSextra) $(CXXFLAGS_main) -D'COMPILE_FOR_LONG_READS' $(CXXFLAGS)
STARlongStatic : LDFLAGS := $(LDFLAGSextra) $(LDFLAGS_static) $(LDFLAGS)
STARlongStatic : Depend.list parametersDefault.xxd $(OBJECTS)
	$(CXX) -o STARlong $(CXXFLAGS) $(OBJECTS) $(LDFLAGS)

gdb : CXXFLAGS := $(CXXFLAGSextra) $(CXXFLAGS_gdb) $(CXXFLAGS)
gdb : LDFLAGS := $(LDFLAGSextra) $(LDFLAGS_gdb) $(LDFLAGS)
gdb : Depend.list parametersDefault.xxd $(OBJECTS)
	$(CXX) -o STAR $(CXXFLAGS) $(OBJECTS) $(LDFLAGS)

gdb-long : CXXFLAGS := $(CXXFLAGSextra) $(CXXFLAGS_gdb) -D'COMPILE_FOR_LONG_READS' $(CXXFLAGS)
gdb-long : LDFLAGS := $(LDFLAGSextra) $(LDFLAGS_gdb) $(LDFLAGS)
gdb-long : Depend.list parametersDefault.xxd $(OBJECTS)
	$(CXX) -o STARlong $(CXXFLAGS) $(OBJECTS) $(LDFLAGS)

STARforMacStatic : CXXFLAGS := $(CXXFLAGSextra) $(CXXFLAGS_main) -D'COMPILE_FOR_MAC' $(CXXFLAGS)
STARforMacStatic : LDFLAGS := $(LDFLAGSextra) $(LDFLAGS_Mac_static) $(LDFLAGS)
STARforMacStatic : Depend.list parametersDefault.xxd $(OBJECTS)
	$(CXX) -o STAR $(CXXFLAGS) $(OBJECTS) $(LDFLAGS)

STARlongForMacStatic : CXXFLAGS := -D'COMPILE_FOR_LONG_READS' $(CXXFLAGSextra) $(CXXFLAGS_main) -D'COMPILE_FOR_MAC' $(CXXFLAGS)
STARlongForMacStatic : LDFLAGS := $(LDFLAGSextra) $(LDFLAGS_Mac_static) $(LDFLAGS)
STARlongForMacStatic : Depend.list parametersDefault.xxd $(OBJECTS)
	$(CXX) -o STARlong $(CXXFLAGS) $(OBJECTS) $(LDFLAGS)
