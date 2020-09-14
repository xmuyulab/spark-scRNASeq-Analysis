#include <fstream>
#include <string>
#include <iostream>
#include <sstream>

using namespace std;

class GetFastq {
    public:

    long i;
    
    stringstream tmpReadIn;

    void tranString(std::string fastqLine);
};