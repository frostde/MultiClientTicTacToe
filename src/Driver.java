import java.io.*;
import java.net.*;

public class Driver {
    private static Socket toServerSocket;
    private static PrintWriter writer;
    private static BufferedReader reader;
    private static BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
    private static DataInputStream inStream;
    private static DataOutputStream outStream;
    private static String line;
    public static char[][] board;
    private static int threadID;


    public static void main(String[] args) {
        System.out.println("CLIENT is attempting connection...");
        initBoard();
        playGame();
    }


    public static void initBoard() {
        board = new char[3][3];
        for (int r = 0; r < board.length; r++) {
            for (int o = 0; o < board[r].length; o++) {
                board[r][o] = ' ';
            }
        }
    }

    public static void playGame() {
        try {
            toServerSocket = new Socket("localhost", 7788);
            if (toServerSocket.isConnected()) {
                System.out.println("CONNECTION HAS BEEN MADE");

                inStream = new DataInputStream(toServerSocket.getInputStream());
                outStream = new DataOutputStream(toServerSocket.getOutputStream());

                writer = new PrintWriter(outStream, true);
                reader = new BufferedReader(new InputStreamReader(inStream));

            } else {
                System.out.println("Failed to make a connection with the server.");
                System.exit(1);
            }

            String[] SystemStatistics = reader.readLine().split(" ");
            System.out.println("So far there are " + SystemStatistics[0] + " wins, " + SystemStatistics[1] + " ties, and " + SystemStatistics[2] + " losses.");

            while ((line = reader.readLine()) != null) {
                String[] fromServerArray = line.split(" ");

                if (fromServerArray.length == 4) {
                    if (fromServerArray[3].equals("TIE")) {
                        printBoard();
                        System.out.println("You and the computer have tied the game... CAT!");
                        System.exit(1);
                    }
                    if (fromServerArray[3].equals("WIN")) {
                        printBoard();
                        System.out.println("You have triumphantly defeated the computer!");
                        System.exit(1);
                    }
                    if (fromServerArray[3].equals("LOSS")) {
                        String[] serverRow = fromServerArray[1].split("#");
                        String[] serverColumn = fromServerArray[2].split("#");
                        board[Integer.parseInt(serverRow[1])][Integer.parseInt(serverColumn[1])] = 'X';
                        printBoard();
                        System.out.println("Sorry, you have lost.");
                        System.exit(1);
                    }
                }
                if (fromServerArray[0].equals("NONE")) {
                    writer.println(getPlayerMove());
                }
                if (fromServerArray[0].equals("MOVE")) {
                    String[] serverRow = fromServerArray[1].split("#");
                    String[] serverColumn = fromServerArray[2].split("#");
                    board[Integer.parseInt(serverRow[1])][Integer.parseInt(serverColumn[1])] = 'X';
                    writer.println(getPlayerMove());
                }
            }
        } catch (Exception ex) {
            String s = "";
        }
    }

    public static void printBoard() {
        System.out.println(" " + board[0][0] + " | " + board[0][1] + " | " + "" + board[0][2]);
        System.out.println("___________");
        System.out.println(" " + board[1][0] + " | " + board[1][1] + " | " + "" + board[1][2]);
        System.out.println("___________");
        System.out.println(" " + board[2][0] + " | " + board[2][1] + " | " + "" + board[2][2]+"\n\n");
    }

    public static String getPlayerMove() throws IOException {

        printBoard();
        int row, column;
        do {
            System.out.println("Please enter a row:\n");
            row = Integer.parseInt(consoleIn.readLine());
            System.out.println("Please enter a column:\n");
            column = Integer.parseInt(consoleIn.readLine());
        } while (board[row][column] != ' ');
        board[row][column] = 'O';
        return "MOVE " + "#" + row + " #" + column;


    }
}
