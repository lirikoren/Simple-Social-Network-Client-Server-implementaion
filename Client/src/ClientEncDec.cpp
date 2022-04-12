//
// Created by Boaz on 1/4/2022.
//
#include "../include/ClientEncDec.h"
#include <sstream>
#include <cstring>
using namespace std;

clientEncDec::clientEncDec():
        outputToPrint(""){
}

vector<char> clientEncDec::encode(string &msg){
    vector<char> output;
    //case 1 - REGISTER
    if(msg.find("REGISTER")!=string::npos)
        output= REGISTER_encode(output,msg);

    //case 2 - LOGIN
    if(msg.find("LOGIN")!=string::npos)
        output= LOGIN_encode(output,msg);

    //case 3 - LOGOUT
    if (msg.find("LOGOUT")!=string::npos)
        output= LOGOUT_encode(output,msg);

    //case 4 - FOLLOw
    if(msg.find("FOLLOW")!=string::npos)
        output= FOLLOW_encode(output,msg);

    //case 5 - POST
    if (msg.find("POST") != string::npos)
        output= POST_encode(output,msg);

    //case 6 - PM
    if(msg.find("PM") != string::npos)
        output= PM_encode(output,msg);

    //case 7 - LOGSTAT
    if(msg.find("LOGSTAT") != string::npos)
        output= LOGSTAT_encode(output,msg);

    //case 8 - STAT
    if(msg.find("STAT") != string::npos)
        output= STAT_encode(output,msg);

    //case 12 - BLOCK
    if(msg.find("BLOCK") != string::npos)
        output= BLOCK_encode(output,msg);
    output.push_back(';');
    return output;
}

vector<char> clientEncDec::REGISTER_encode(vector<char> output,string &msg){
    string username;
    string parts;
    string password;
    string birthday;
    istringstream input_String(msg);
    int j = 1;
    while (getline(input_String, parts, ' ')) {
        // j==1 - reading the opcode --> continue
        if (j == 1) {
            j = 2;
            continue;
        }
            //j==2 - reading the username
        else if (j == 2) {
            username = parts;
            j++;
            continue;
        }
            //j==3 - reading the password
        else if (j == 3) {
            password = parts;
            j++;
            continue;
        }
            //j==4 - reading the birthday
        else {
            birthday = parts;
        }
    }

    shortToBytes(1, opcode);

    output.push_back(opcode[0]);
    output.push_back(opcode[1]);
    for (int i = 0; (unsigned)i < username.length(); i++) {
        output.push_back(username[i]);
    }
    output.push_back('\0');
    for (int i = 0;(unsigned) i < password.length(); i++) {
        output.push_back(password[i]);
    }
    output.push_back('\0');
    for (int i = 0;(unsigned) i < birthday.length(); i++) {
        output.push_back(birthday[i]);
    }
    output.push_back('\0');
    return output;
}

vector<char> clientEncDec::LOGIN_encode(vector<char> output,string &msg){
    string username;
    string parts;
    string password;
    string captcha;
    istringstream input_String(msg);
    int j = 1;
    while (getline(input_String, parts, ' ')) {
        // j==1 - reading the opcode --> continue
        if (j == 1) {
            j = 2;
            continue;
        }
            //j==2 - reading the username
        else if (j == 2) {
            username = parts;
            j++;
            continue;
        }
            //j==3 - reading the password
        else if (j == 3) {
            password = parts;
            j++;
            continue;
        }
            //j==4 - reading the captcha
        else {
            captcha = parts;
        }
    }

    shortToBytes(2, opcode);

    output.push_back(opcode[0]);
    output.push_back(opcode[1]);
    for (int i = 0; (unsigned)i < username.length(); i++) {
        output.push_back(username[i]);
    }
    output.push_back('\0');
    for (int i = 0;(unsigned) i < password.length(); i++) {
        output.push_back(password[i]);
    }
    output.push_back('\0');
    for (int i = 0;(unsigned) i < captcha.length(); i++) {
        output.push_back(captcha[i]);
    }
    return output;
}

vector<char> clientEncDec::LOGOUT_encode(vector<char> output,string &msg) {
    shortToBytes(3,result);
    output.push_back(result[0]);
    output.push_back(result[1]);
    return output;
}

vector<char> clientEncDec::FOLLOW_encode(vector<char> output, string &msg){
   string parts;
   char followUnfollow = -1;
   string username;
   istringstream input_string(msg);
   int j=1;
   while (getline(input_string,parts,' ')){
       // j==1 - reading the opcode --> continue
       if (j == 1) {
           j = 2;
           continue;
       }
           //j==2 - reading the follow unfollow
       else if (j == 2) {
           if(parts[0]=='0')
               followUnfollow='\0';
           else
               followUnfollow='\1';
           j++;
           continue;
       }
           //j==3 - reading username
       else {
           username = parts;
       }
   }
    shortToBytes(4,opcode);
    output.push_back(opcode[0]);
    output.push_back(opcode[1]);
    output.push_back(followUnfollow);
    for (int i = 0; (unsigned)i < username.size(); i++) {
        output.push_back(username[i]);
    }
    output.push_back('\0');
    return output;
}

vector<char> clientEncDec::POST_encode(vector<char> output,string &msg){
    string parts;
    istringstream input_String(msg);
    string content;
    getline(input_String,parts,' ');
    getline(input_String,parts);
    int partSize = parts.length();
    shortToBytes(5,opcode);
    output.push_back(opcode[0]);
    output.push_back(opcode[1]);
    for(int i = 0 ; (unsigned) i < (unsigned)partSize; i++){
        output.push_back(parts[i]);
    }
    output.push_back('\0');
    return output;
}

vector<char> clientEncDec::PM_encode(vector<char> output,string &msg){
    string username;
    string parts;
    istringstream input_String(msg);
    string content;
    getline(input_String,parts,' ');
    getline(input_String,username,' ');
    getline(input_String,parts);
    int partSize = parts.length();
    shortToBytes(6,opcode);
    output.push_back(opcode[0]);
    output.push_back(opcode[1]);
    for (int i = 0; (unsigned)i < username.length(); i++) {
        output.push_back(username[i]);
    }
    output.push_back('\0');
    for (int i = 0; (unsigned) i < (unsigned)partSize ; i++){
        output.push_back(parts[i]);
    }
    output.push_back('\0');
    return output;
}

vector<char> clientEncDec::LOGSTAT_encode(vector<char> output,string &msg){
    shortToBytes(7,result);
    output.push_back(result[0]);
    output.push_back(result[1]);
    return output;
}

vector<char> clientEncDec::STAT_encode(vector<char> output,string &msg){
    string parts;
    int numOfUsers = 0;
    string users;
    istringstream input_String(msg);
    int j = 1;
    shortToBytes(8, opcode);
    output.push_back(opcode[0]);
    output.push_back(opcode[1]);
    while (getline(input_String, parts, ' ')) {
        // j==1 - reading the opcode --> continue
        if (j == 1) {
            j = 2;
            continue;
        }
        else {
            for(int i =0 ; (unsigned)i <(unsigned) parts.size();i++) {
                output.push_back(parts[i]);
            }
            output.push_back('|');
            j++;
        }
    }
    return output;
}

vector<char> clientEncDec::BLOCK_encode(vector<char> output,string &msg){
    string username;
    string parts;
    istringstream input_String(msg);
    int j = 1;
    while (getline(input_String, parts, ' ')) {
        // j==1 - reading the opcode --> continue
        if (j == 1) {
            j = 2;
            continue;
        }
            //j==2 - reading the username
        else{
            username = parts;
            j++;
            continue;
        }
    }

    shortToBytes(12, opcode);
    output.push_back(opcode[0]);
    output.push_back(opcode[1]);
    for (int i = 0; (unsigned)i < username.length(); i++) {
        output.push_back(username[i]);
    }
    output.push_back('\0');
    return output;

}


string clientEncDec::buildMessage() {
    outputToPrint="";
    if (len >= 2) {
        char opcodeBytes[2];
        opcodeBytes[0] = bytes[0];
        opcodeBytes[1] = bytes[1];
        switch (clientEncDec::bytesToShort(opcodeBytes)) {
            //---notification---
            case (9): {
                outputToPrint = "NOTIFICATION ";
                char notificationType = bytes[2];
                if (notificationType == '\0')
                    outputToPrint += "PM ";
                else
                    outputToPrint += "Public ";
                int curr = 3;
                vector<char>userNameBytes;
                while(bytes[curr]!='\0'){
                    userNameBytes.push_back((char)bytes[curr]);
                    curr++;
                }
                char temp[userNameBytes.size()];
                for(int i=0;i<userNameBytes.size();i++)
                    temp[i]=userNameBytes.at(i);
                string s(temp,userNameBytes.size());
                outputToPrint+=s;
                curr++;
                vector<char>contentBytes;
                while(bytes[curr]!='\0'){
                    contentBytes.push_back((char)bytes[curr]);
                    curr++;
                }
                char temp2[contentBytes.size()];
                outputToPrint+=" ";
                for(int i=0;i<contentBytes.size();i++)
                    temp2[i]=contentBytes.at(i);
                string s2(temp2,contentBytes.size());
                outputToPrint+=s2;
                break;
            }
                //--------ACK-------
            case (10): {
                outputToPrint = "ACK ";
                char msgOpcodeBytes[2];
                msgOpcodeBytes[0] = bytes[2];
                msgOpcodeBytes[1] = bytes[3];
                outputToPrint+= to_string(clientEncDec::bytesToShort(msgOpcodeBytes))+" ";
                switch (clientEncDec::bytesToShort(msgOpcodeBytes)) {
                    case(4):{ //FollowUnfollow
                        int curr=4;
                        vector<char>userNameBytes;
                        while(bytes[curr]!='\0'){
                            userNameBytes.push_back((char)bytes[curr]);
                            curr++;
                        }
                        char temp[userNameBytes.size()];
                        for(int i=0;i<userNameBytes.size();i++)
                            temp[i]=userNameBytes.at(i);
                        string s(temp,userNameBytes.size());
                        outputToPrint+=s;
                        break;
                    }
                    case(8):
                    case (7):{  //LOGSTAT and STAT
                        char age[2];
                        age[0] = bytes[4];
                        age[1] = bytes[5];
                        char numPosts[2];
                        numPosts[0] = bytes[6];
                        numPosts[1] = bytes[7];
                        char numFollowers[2];
                        numFollowers[0] = bytes[8];
                        numFollowers[1] = bytes[9];
                        char numFollowing[2];
                        numFollowing[0] = bytes[10];
                        numFollowing[1] = bytes[11];
                        outputToPrint+= " "+to_string(clientEncDec::bytesToShort(age));
                        outputToPrint+= " "+to_string(clientEncDec::bytesToShort(numPosts));
                        outputToPrint+= " "+to_string(clientEncDec::bytesToShort(numFollowers));
                        outputToPrint+= " "+to_string(clientEncDec::bytesToShort(numFollowing));
                        break;
                    }
                }
                break;
            }
            case(11):{
                outputToPrint="ERROR ";
                char msgOpcode[2];
                msgOpcode[0] = bytes[2];
                msgOpcode[1] = bytes[3];
                outputToPrint+= to_string(bytesToShort(msgOpcode));
                break;
            }
        }
    }
    len=0;
    bytes.clear();
    return outputToPrint;
}

string clientEncDec::decodeNextByte(char byte) {
    if (byte == ';') {
        //cout<<"received bytes:"+ to_string(len)<<endl;
        return buildMessage();
    }
    //cout<<"recieved byte:"+ to_string(byte)<<endl;
    bytes.push_back(byte);
    len++;
    return "";
}

void clientEncDec::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}
short clientEncDec::bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
