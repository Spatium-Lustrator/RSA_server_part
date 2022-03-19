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

    // Need change when work on another computer
    String mainPath = "C:\\Users\\t.dmitrieva\\IdeaProjects\\ServerPart\\src\\";

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

                        Path path = methodsExecutor.createFile(request);

                        OutputStream fileOS = new FileOutputStream(String.valueOf(path), true);
                        String passwordLine = "P: " + request.substring(14);
                        fileOS.write(passwordLine.getBytes());
                        fileOS.write("\r\n".getBytes());
                        fileOS.write("F: ".getBytes());
                        fileOS.write("\r\n".getBytes());
                        fileOS.write("NA: ".getBytes());
                        //fileOS.write("\r\n".getBytes());  // For jsoup part
                        //fileOS.write("NC: ".getBytes());

                        fileOS.close();


                        response = (String) "SUCCESSFUL: Sign Up";

                    } else if (Integer.parseInt(request.substring(0, 2)) == 2) { // Add friend request

                        if(Files.exists(Path.of(methodsExecutor.mainPath + request.substring(2, 13) + ".txt"))) {

                            methodsExecutor.changeFile("F:",0, request.substring(2, 13),
                                    request.substring(13));

                            response = "SUCCESSFUL: ADD FRIEND";

                        } else {
                            response = "FAILED OPERATION: ADD FRIEND. USER ISN`T EXISTS";
                        }

                    } else if (Integer.parseInt(request.substring(0, 2)) == 3) { // NA-request

                        String friends = methodsExecutor.readFile(request.substring(13), "F:");

                        for (int j = 0; j<friends.length(); j += 11) { // Friends loop

                            methodsExecutor.changeFile("NA:", 0,  request.substring(2, 13),
                                    friends.substring(j, j+11));

                        }

                        response = "SUCCESSFUL: NEED-ATTENTION-request";


                    } else if (Integer.parseInt(request.substring(0, 2)) == 4) { // CHECK_NA-request

                        String lineNA = methodsExecutor.readFile(request.substring(13), "NA:");

                        if (lineNA.length() > 3) {

                            response = lineNA.replaceAll("(.{11})", "$1, ");
                            response = "1" + response.substring(0, response.length()-2);

                            methodsExecutor.changeFile("NA:", 1, "", request.substring(13));


                        } else {

                            response = "CHECK_NA-request is successful. Nobody want attention";

                        }

                    } else if (Integer.parseInt(request.substring(0, 2)) == 5) {

                        if(Files.exists(Path.of(methodsExecutor.mainPath + request.substring(2, 13) + ".txt"))) {

                            String usersPassword = methodsExecutor.readFile(request.substring(2, 13), "P:");

                            if (usersPassword.equals(request.substring(13))) {

                                response = "SUCCESSFUL: SIN-request";

                            } else {
                                response = "ER02 FAILED: SIN-request";
                            }
                        } else {

                            response = "ER01 FAILED: SIN-request";

                        }


                        // Big chance what this method is not useful, because of new algorithm
//                    } else if (Integer.parseInt(request.substring(0, 2)) == 6) {    // Singer Request
//
//                        if (Files.exists(Path.of(methodsExecutor.mainPath + request.substring(2, 13) + ".txt"))) {
//                            methodsExecutor.changeFile("NC:", 0, request.substring(13),
//                                    request.substring(2, 13));
//                            response = "SUCCESSFUL: SINGER-request";
//                        } else {
//                            response = "ER01 FAILED: SINGER-request";
//                        }
//
//                    } else if (Integer.parseInt(request.substring(0, 2)) == 7) { // Want to concert request
//
//                        String friends = methodsExecutor.readFile(request.substring(13), "F:");
//
//                        for (int j = 0; j<friends.length(); j += 11) { // Friends loop
//
//                            methodsExecutor.changeFile("NC:", 0,  request.substring(2, 13),
//                                    friends.substring(j, j+11));
//
//                        }

//                    } else if (Integer.parseInt(request.substring(0, 2)) == 8) { // Check NC-string request
//
//                        String lineNC = methodsExecutor.readFile(request.substring(2, 13), "NC:");
//
//                        if (lineNC.length() > 3) {
//
//                            response = lineNC.replaceAll("(.{11})", "$1, ");
//                            response = "1" + response.substring(0, response.length()-2);
//
//                            methodsExecutor.changeFile("NC:", 1, "", request.substring(13));
//
//                        } else {
//
//                            response = "Nobody want to visit concert";
//
//                        }

                    }

                    System.out.println("Response: " + response);
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

    private void changeFile(String beginOfLine, Integer task, String forWrite, String fileTo) throws IOException {

        // Arrays
        List<String> lines = Files.readAllLines(Paths.get
                    (mainPath + fileTo + ".txt"), StandardCharsets.UTF_8);

        List<String> toWrite = new ArrayList<>();

        // reading file and changing line
        for (int i = 0; i<lines.size(); i++) {

            String line = lines.get(i);

            if (line.startsWith(beginOfLine) & task == 0) {

                String updated = line.trim() + " " + forWrite;
                toWrite.add(updated);


            } else if (line.startsWith(beginOfLine) & task == 1){   // Clear string

                String updated = beginOfLine;
                toWrite.add(updated);



            } else {
                toWrite.add(line);

            }
        }

        File file = new File(mainPath + fileTo + ".txt");
        if (file.exists()) {

            file.delete();
            file.createNewFile();
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
