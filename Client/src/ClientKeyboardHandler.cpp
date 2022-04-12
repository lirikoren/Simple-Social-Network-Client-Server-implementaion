//
// Created by Boaz on 1/4/2022.
//
using namespace std;
#include "../include/ClientKeyboardHandler.h"
ClientKeyboardHandler::ClientKeyboardHandler():shouldStop(false),loggedIn(false) {}



void ClientKeyboardHandler::run(ConnectionHandler &ch, clientEncDec &decoder) {
    while(!shouldStop){
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        std::vector<char> output=decoder.encode(line);
        if(!loggedIn && line.find("LOGIN")!=string::npos){
            loggedIn=true;
        }
         if(loggedIn && line.find("LOGOUT")!=string::npos){
            shouldStop=true;
        }
        char tmp[output.size()];
        for(int i=0;i<output.size();i++)
            tmp[i]=output.at(i);
        if (!ch.sendBytes(tmp,output.size())) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        // connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
        //std::cout << "Sent " << output.size() << " bytes to server" << std::endl;
    }
}



