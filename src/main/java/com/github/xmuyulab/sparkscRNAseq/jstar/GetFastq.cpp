#include <fstream>
#include <string>
#include <iostream>
#include <sstream>
#include "GetFastq.h"

void GetFastq::tranString(std::string fastqLine) {
    //  readIn可以看作是STAR原始的fastq文件输入流
    //  readIn.open("/home/liuyu/fastq", std::ios::app);
    tmpReadIn << fastqLine;
    //  readIn << tmpReadIn.rdbuf();
    //  readIn.open("/home/liuyu/fastq", std::ios::app);
    //  readIn.close();
};