//Объект возвращаемого сообщения

import java.nio.file.Path;

public class Answer {

    private byte[] message;
    private byte[] content;
    private Path filePath;

    Answer(byte[] message, byte[] content, Path filePath){
        this.message = message;
        this.content = content;
        this.filePath = filePath;
    }

    Answer(byte[] message){
        this.message = message;
        this.content = null;
        this.filePath = null;
    }


    public byte[] getContent() {
        return content;
    }

    public byte[] getMessage() {
        return message;
    }

    public Path getFilePath() {
        return filePath;
    }
}
