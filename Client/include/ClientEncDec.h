//
// Created by Boaz on 1/4/2022.
//

#ifndef ASS3CLIENT_CLIENTENCDEC_H
#define ASS3CLIENT_CLIENTENCDEC_H
#include <iostream>
#include <vector>
#include <thread>
using namespace std;

class clientEncDec{

public:
    clientEncDec();

    string decodeNextByte(char byte);

    vector<char> encode(string& msg);
    string buildMessage();

        private:
    short bytesToShort(char *bytesArr);
    void shortToBytes(short num,char *bytesArr);
    string outputToPrint;
    char result[2];
    char opcode[2];
    vector<char> bytes;
    int len;

    //encode
    vector<char> REGISTER_encode(vector<char> output, string &msg);
    vector<char> LOGIN_encode(vector<char> output, string &msg);
    vector<char> LOGOUT_encode(vector<char> output, string &msg);
    vector<char> FOLLOW_encode(vector<char> output, string &msg);
    vector<char> POST_encode(vector<char> output, string &msg);
    vector<char> PM_encode(vector<char> output, string &msg);
    vector<char> LOGSTAT_encode(vector<char> output, string &msg);
    vector<char> STAT_encode(vector<char> output, string &msg);
    vector<char> BLOCK_encode(vector<char> output, string &msg);



};


#endif //ASS3CLIENT_CLIENTENCDEC_H
