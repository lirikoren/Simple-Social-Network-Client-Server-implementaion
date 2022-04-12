#include <stdlib.h>
#include "../include/ConnectionHandler.h"
#include "../include/ClientKeyboardHandler.h"
#include "../include/ClientEncDec.h"
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    ClientKeyboardHandler clientKeyboardHandler;
    clientEncDec clientEncDec;
    //creates thread for Keyboard listener, and this main thread will listen for answers from server
    thread keyboardListenerThread(&ClientKeyboardHandler::run,&clientKeyboardHandler, ref(connectionHandler),ref(clientEncDec));


    bool isConnected=true;
    //From here we will see the rest of the ehco client implementation:
    while (isConnected) {
        string answer="";
        // We can use one of three options to read data from the server:
        // 1. Read a fixed number of characters
        // 2. Read a line (up to the newline character using the getline() buffered reader
        // 3. Read up to the null character
        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
        char nextByte[1];
        if (!connectionHandler.getBytes(nextByte,1)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        answer=clientEncDec.decodeNextByte(nextByte[0]);
        if(answer.size()>0){
            cout<<"answer:"+answer<<endl;
            if(answer.find("ACK 3")!=-1){
                isConnected=false;
            }
        }
    }
    keyboardListenerThread.join();
    return 0;
}
