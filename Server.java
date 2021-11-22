import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Server {

    String mainPath = "C:\\Users\\Toma\\IdeaProjects\\ServerApp\\Server\\src\\";

    public static void main(String[] args) throws IOException {



        try (ServerSocket server = new ServerSocket(8000)) {

            System.out.println("Server started!");

            while (true) {
                try (
                        Socket socket = server.accept();
                        BufferedWriter writer =
                                new BufferedWriter(
                                        new OutputStreamWriter(
                                                socket.getOutputStream()));

                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                socket.getInputStream()));

                ) {

                    Server methodsExecutor = new Server();

                    System.out.println("Client connected!");

                    String request = reader.readLine();

                    System.out.println("Request: " + request);

                    String response = "";

                    if (Integer.parseInt(request.substring(0, 2)) == 1) { // Register request

                        System.out.println("Begin of signing up");

                        Path path = methodsExecutor.createFile(request);

                        OutputStream fileOS = new FileOutputStream(String.valueOf(path), true);

                        fileOS.write(request.substring(14).getBytes());
                        fileOS.write("\r\n".getBytes());
                        fileOS.write("F: ".getBytes());
                        fileOS.write("\r\n".getBytes());
                        fileOS.write("NA: ".getBytes());

                        fileOS.close();


                        response = (String) "SUCCESSFUL: Sign Up";

                    } else if (Integer.parseInt(request.substring(0, 2)) == 2) { // Add friend request

                        if(Files.exists(Path.of(methodsExecutor.mainPath + request.substring(2, 13) + ".txt"))) {

                            methodsExecutor.changeFile("F:", request.substring(2, 13),
                                    request.substring(13));

                            response = "SUCCESSFUL: ADD FRIEND";

                        } else {
                            response = "FAILED OPERATION: ADD FRIEND. USER IS`NT EXISTS";
                        }

                    } else if (Integer.parseInt(request.substring(0, 2)) == 3) { // NA-request

                        String friends = methodsExecutor.readFile(request.substring(13), "F:");

                        for (int j = 0; j<friends.length(); j += 11) { // Friends loop

                            methodsExecutor.changeFile("NA:", request.substring(2, 13),
                                    friends.substring(j, j+11));

                        }

                        response = "SUCCESSFUL: NEED-ATTENTION-request";


                    } else if (Integer.parseInt(request.substring(0, 2)) == 4) { // CHECK_NA-request

                        String lineNA = methodsExecutor.readFile(request.substring(13), "NA:");

                        if (lineNA.length() > 3) {

                            response = lineNA.replaceAll("(.{11})", "$1, ");
                            response = "1" + response.substring(0, response.length()-2);
                            System.out.println(response);

                        } else {

                            response = "CHECK_NA-request is successful. Nobody want attention";

                        }

                    }

                    writer.write(response);
                    writer.newLine();
                    writer.flush();

                }

            }



        }
    }

    private Path createFile(String request) throws IOException {

        Path path = Path.of( mainPath
                + request.substring(2, 13) + ".txt");
        Files.createFile(path);

        return path;
    }

    private void changeFile(String beginOfLine, String forWrite, String fileTo) throws IOException {

        // Arrays
        List<String> lines = Files.readAllLines(Paths.get
                    (mainPath + fileTo + ".txt"), StandardCharsets.UTF_8);

        List<String> toWrite = new ArrayList<>();

        // reading file and changing line
        for (int i = 0; i<lines.size(); i++) {

            String line = lines.get(i);

            if (line.startsWith(beginOfLine)) {

                String updated = line.trim() + " " +  forWrite;
                toWrite.add(updated);

            } else {
                toWrite.add(line);
            }
        }



        Files.write(
                Paths.get(mainPath + fileTo + ".txt"),
                toWrite,
                StandardCharsets.UTF_8,
                StandardOpenOption.WRITE

        );


    }

    private String readFile(String fileTo, String beginOfString) throws IOException {

        List<String> lines = Files.readAllLines(Paths.get
                (mainPath + fileTo + ".txt"), StandardCharsets.UTF_8);

        String result = "";

        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i);

            if (line.startsWith(beginOfString)) {
                result = line.substring(3);
                break;
            }

        }

        result = result.replace(" ", "");

        return result;

    }


}
