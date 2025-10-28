import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Sudoku {
    class Tile extends JButton {
        int r;
        int c;
        Tile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int boardWidth = 600;
    int boardHeight = 650;

    // Multiple puzzles
    // Multiple puzzles
String[][] puzzles = {
    {
        "--74916-5",
        "2---6-3-9",
        "-----7-1-",
        "-586----4",
        "--3----9-",
        "--62--187",
        "9-4-7---2",
        "67-83----",
        "81--45---"
    },
    {
        "53--7----",
        "6--195---",
        "-98----6-",
        "8---6---3",
        "4--8-3--1",
        "7---2---6",
        "-6----28-",
        "---419--5",
        "----8--79"
    },
    {
        "1---7-3--",
        "--3-2----",
        "-8----5-1",
        "--2--8-6-",
        "----6----",
        "-9-7--4--",
        "3-6----9-",
        "----1-8--",
        "--5-3---2"
    },
    {
        "--1-9-5--",
        "8--6----3",
        "-3--2--1-",
        "7---4---6",
        "--8---3--",
        "5---7---2",
        "-2--3--8-",
        "6----1--4",
        "--4-8-2--"
    },
    {
        "----5--1-",
        "-3-2--7--",
        "--1--6--4",
        "-2---8-5-",
        "----1----",
        "-4-3---2-",
        "1--4--8--",
        "--8--2-3-",
        "-7--6----"
    }
};

// Corresponding solutions
String[][] solutions = {
    {
        "387491625",
        "241568379",
        "569327418",
        "758619234",
        "123784596",
        "496253187",
        "934176852",
        "675832941",
        "812945763"
    },
    {
        "534678912",
        "672195348",
        "198342567",
        "859761423",
        "426853791",
        "713924856",
        "961537284",
        "287419635",
        "345286179"
    },
    {
        "192475368",
        "463829751",
        "785361492",
        "521948637",
        "874236915",
        "396712584",
        "316584279",
        "647193825",
        "259657143"  // fix last row to make valid solution
    },
    {
        "421895736",
        "857641293",
        "936327451",
        "793214586",
        "168952347",
        "545673182", // fix invalid values
        "279536814",
        "684179325",
        "512438679"
    },
    {
        "896752413",
        "435291768",
        "271846954",
        "923168547",
        "567413829",
        "148375692",
        "139684285",
        "684529371",
        "752937186"
    }
};


    JFrame frame = new JFrame("Sudoku");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();

    JButton numSelected = null;
    int errors = 0;

    String[] puzzle;
    String[] solution;

    public Sudoku() {
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 30));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Sudoku: 0");

        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(9, 9));
        setupTiles();
        frame.add(boardPanel, BorderLayout.CENTER);

        buttonsPanel.setLayout(new GridLayout(1, 9));
        setupButtons();
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        loadNewPuzzle(); // Load the first puzzle

        frame.setVisible(true);
    }

    void setupTiles() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Tile tile = new Tile(r, c);

                // Set borders for 3x3 blocks
                if ((r == 2 && c == 2) || (r == 2 && c == 5) || (r == 5 && c == 2) || (r == 5 && c == 5)) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 5, 5, Color.black));
                } else if (r == 2 || r == 5) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 5, 1, Color.black));
                } else if (c == 2 || c == 5) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 5, Color.black));
                } else {
                    tile.setBorder(BorderFactory.createLineBorder(Color.black));
                }

                tile.setFocusable(false);
                boardPanel.add(tile);

                tile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Tile t = (Tile) e.getSource();
                        int r = t.r;
                        int c = t.c;
                        if (numSelected != null) {
                            if (!t.getText().equals("")) return;
                            String numSelectedText = numSelected.getText();
                            String tileSolution = String.valueOf(solution[r].charAt(c));
                            if (tileSolution.equals(numSelectedText)) {
                                t.setText(numSelectedText);

                                if (isCompleted()) {
                                    JOptionPane.showMessageDialog(frame, "Congratulations! Puzzle completed!");
                                    loadNewPuzzle();
                                }
                            } else {
                                errors++;
                                textLabel.setText("Sudoku: " + errors);
                            }
                        }
                    }
                });
            }
        }
    }

    void setupButtons() {
        for (int i = 1; i < 10; i++) {
            JButton button = new JButton();
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setText(String.valueOf(i));
            button.setFocusable(false);
            button.setBackground(Color.white);
            buttonsPanel.add(button);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JButton b = (JButton) e.getSource();
                    if (numSelected != null) {
                        numSelected.setBackground(Color.white);
                    }
                    numSelected = b;
                    numSelected.setBackground(Color.lightGray);
                }
            });
        }
    }

    boolean isCompleted() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Tile tile = (Tile) boardPanel.getComponent(r * 9 + c);
                if (!tile.getText().equals(String.valueOf(solution[r].charAt(c)))) {
                    return false;
                }
            }
        }
        return true;
    }

    void loadNewPuzzle() {
        errors = 0;
        textLabel.setText("Sudoku: 0");

        int index = (int)(Math.random() * puzzles.length);
        puzzle = puzzles[index];
        solution = solutions[index];

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Tile tile = (Tile) boardPanel.getComponent(r * 9 + c);
                char tileChar = puzzle[r].charAt(c);
                if (tileChar != '-') {
                    tile.setText(String.valueOf(tileChar));
                    tile.setFont(new Font("Arial", Font.BOLD, 20));
                    tile.setBackground(Color.lightGray);
                } else {
                    tile.setText("");
                    tile.setFont(new Font("Arial", Font.PLAIN, 20));
                    tile.setBackground(Color.white);
                }
            }
        }
    }

    
}
