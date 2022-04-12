//
// Created by Boaz on 1/4/2022.
//
#ifndef ASS3CLIENT_CLIENTKEYBOARDHANDLER_H
#define ASS3CLIENT_CLIENTKEYBOARDHANDLER_H
#include "ConnectionHandler.h"
#include "ClientEncDec.h"

class ClientKeyboardHandler{
private:
    bool shouldStop;
    bool loggedIn;
public:
    ClientKeyboardHandler();
    void run(ConnectionHandler &ch, clientEncDec &decoder);
};


#endif //ASS3CLIENT_CLIENTKEYBOARDHANDLER_H
