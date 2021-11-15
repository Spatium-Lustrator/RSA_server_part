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

    public static void main(String[] args) throws IOException {

        String mainPath = "C:\\Users\\Toma\\IdeaProjects\\ServerApp\\Server\\src\\";

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
                    System.out.println("Client connected!");
                    String request = reader.readLine();
                    System.out.println("Request: " + request);
                    String response = "";

                    if (Integer.parseInt(request.substring(0, 2)) == 1) {
                        System.out.println("Begin of signing up");
                        Path path = Path.of(mainPath
                                + request.substring(2, 13) + ".txt");
                        Files.createFile(path);

                        OutputStream fileOS = new FileOutputStream(String.valueOf(path), true);

                        fileOS.write(request.substring(14).getBytes());
                        fileOS.write("\r\n".getBytes());
                        fileOS.write("F: ".getBytes());
                        fileOS.write("\r\n".getBytes());
                        fileOS.write("NA: ".getBytes());

                        fileOS.close();


                        response = (String) "SUCCESSFUL: Sign Up";

                    } else if (Integer.parseInt(request.substring(0, 2)) == 2) {

                        if(Files.exists(Path.of(mainPath + request.substring(2, 13) + ".txt"))) {

                            List<String> lines = Files.readAllLines(Paths.get
                                    (mainPath + request.substring(14) + ".txt"), StandardCharsets.UTF_8);

                            List<String> toWrite = new ArrayList<>();
                            for (int i = 0; i<lines.size(); i++) {

                                String line = lines.get(i);

                                if (line.startsWith("F:")) {

                                    String updated = line.trim() + " " +  request.substring(2, 13);
                                    toWrite.add(updated);

                                } else {
                                    toWrite.add(line);
                                }
                            }



                            Files.write(
                                    Paths.get(mainPath + request.substring(14) + ".txt"),
                                    toWrite,
                                    StandardCharsets.UTF_8,
                                    StandardOpenOption.WRITE

                                    );

                            response = "SUCCESSFUL: ADD FRIEND";

                        } else {
                            response = "FAILED OPERATION: ADD FRIEND. USER IS`NT EXISTS";
                        }
                    }



                    writer.write(response);
                    writer.newLine();
                    writer.flush();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    public void createFile(String path, String line) throws IOException {

        Files.createFile(Path.of(path));

    }
}
