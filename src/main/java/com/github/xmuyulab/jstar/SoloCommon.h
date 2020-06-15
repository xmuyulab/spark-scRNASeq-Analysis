#ifndef H_SoloCommon
#define H_SoloCommon

typedef struct{
    uint64 cb; 
    uint32 umi;
} readInfoStruct;

typedef struct{
    uint32 tr; 
    uint8  type;
} trTypeStruct;

typedef uint32 typeUMI;



#define typeUMIbits 32
#define velocytoTypeGeneBits 4
#define velocytoTypeGeneBitShift 28

#endif