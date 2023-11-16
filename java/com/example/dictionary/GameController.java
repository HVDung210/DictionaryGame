package com.example.dictionary;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    private ImageView letterImageView;

    @FXML
    private Button button;

    @FXML
    private Pane pane1;

    @FXML
    private Pane pane2;

    @FXML
    private Pane pane3;

    @FXML
    private Pane pane4;

    @FXML
    private Pane pane5;

    @FXML
    private Pane pane6;

    @FXML
    private Pane pane7;

    @FXML
    private ImageView paneImage1;

    @FXML
    private ImageView paneImage2;

    @FXML
    private ImageView paneImage3;

    @FXML
    private ImageView paneImage4;

    @FXML
    private ImageView paneImage5;

    @FXML
    private ImageView paneImage6;

    @FXML
    private ImageView paneImage7;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label wordExplain;

    List<Pane> panes = new ArrayList<>();
    List<String> wordList = new ArrayList<>();
    List<String> wordExplainList = new ArrayList<>();
    List<Character> letterList = new ArrayList<>();
    List<Character> letterInPane = new ArrayList<>();
    List<ImageView> imageViewList = new ArrayList<>();
    private List<Boolean> isEmptyPane;
    Random random = new Random();
    Timeline timeline = new Timeline();
    private int randomIndex;
    private double velocity;
    public static final double acceleration = 0.01;
    private int currentWordIndex;
    private int score;
    private int posIndex;
    private int midIndex;
    private char currentLetter;
    private String currentWord;
    private String imagePath;
    private boolean isLose;

    public void loadWordsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\BTL\\Dictionary\\src\\main\\resources\\com\\example\\dictionary\\words.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordList.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectRandomWord() {
        currentWordIndex = random.nextInt(wordList.size());
        currentWord = wordList.get(currentWordIndex);
        midIndex = (currentWord.length() + 1) / 2;
        posIndex = midIndex;
        wordList.remove(currentWordIndex);
    }

    public void displayWordExplain() {
        String currentWordExplain = wordExplainList.get(currentWordIndex);
        wordExplain.setText(currentWordExplain);
        wordExplainList.remove(currentWordIndex);
    }

    public void loadWordExplainFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\BTL\\Dictionary\\src\\main\\resources\\com\\example\\dictionary\\word_explain.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordExplainList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayCurrentLetter() {
        for (int i = 0; i < currentWord.length(); i++) {
            letterList.add(currentWord.charAt(i));
        }

        randomIndex = random.nextInt(letterList.size());
        currentLetter = letterList.get(randomIndex);
        imagePath = "D:\\BTL\\Dictionary\\src\\main\\resources\\com\\example\\dictionary\\letterImage\\" + Character.toLowerCase(currentLetter) + ".jpg";
        letterImageView.setImage(new Image(imagePath));
        letterList.remove(randomIndex);
    }

    public double getLayoutXPane(int posIndex) {
        switch (posIndex) {
            case 1 -> {
                return pane1.getLayoutX();
            }
            case 2 -> {
                return pane2.getLayoutX();
            }
            case 3 -> {
                return pane3.getLayoutX();
            }
            case 4 -> {
                return pane4.getLayoutX();
            }
            case 5 -> {
                return pane5.getLayoutX();
            }
            case 6 -> {
                return pane6.getLayoutX();
            }
            case 7 -> {
                return pane7.getLayoutX();
            }
            default -> {
            }
        }

        return 0;
    }

    public void moveLeft() {
        if (posIndex > 1) {
            posIndex--;

            if (letterImageView.getLayoutY() + letterImageView.getFitHeight() + velocity <= pane1.getLayoutY()) {
                letterImageView.setLayoutY(letterImageView.getLayoutY() + velocity * 4);
            }
        }

        letterImageView.setLayoutX(getLayoutXPane(posIndex));
    }

    public void moveRight() {
        if (posIndex < currentWord.length()) {
            posIndex++;

            if (letterImageView.getLayoutY() + letterImageView.getFitHeight() + velocity <= pane1.getLayoutY()) {
                letterImageView.setLayoutY(letterImageView.getLayoutY() + velocity * 4);
            }
        }

        letterImageView.setLayoutX(getLayoutXPane(posIndex));
    }

    public void moveDown() {
        letterImageView.setLayoutY(letterImageView.getLayoutY() + 50);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        score = 0;
        isLose = false;
        button.setManaged(false);
        scoreLabel.setText(String.valueOf(score));
        loadWordsFromFile();
        loadWordExplainFromFile();
        selectRandomWord();
        displayWordExplain();
        displayCurrentLetter();
        addPane();
        addLetterInPane();
        loadLevel();
        addImageView();
        fallingDown();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        letterImageView.setLayoutX(getLayoutXPane(posIndex));
    }

    public void fallingDown() {
        velocity = 0;
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.017),
                event -> {
                    if (checkLetterOnPane()) {
                        letterImageView.setLayoutY(letterImageView.getLayoutY() + velocity);
                        velocity += acceleration;
                    } else {
                        if (isEmptyPane.get(posIndex - 1)) {
                            setPane();
                            letterInPane.set(posIndex - 1, currentLetter);
                            setLetterImageOnPane();
                            isEmptyPane.set(posIndex - 1, false);

                            if (createNewWord()) {
                                velocity = 0;
                                if (isCorrectWord()) {
                                    correctWord();
                                } else {
                                    incorrectWord();

                                    return;
                                }
                            }

                            posIndex = midIndex;
                            setImageView();
                            velocity = 0;
                        } else {
                            isLose = true;
                            button.setManaged(true);
                            timeline.pause();

                            System.out.println("lose");
                        }
                    }
                }
        ));
    }

    @FXML
    void moveKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case LEFT -> {
                if (!isLose) {
                    moveLeft();
                }
            }
            case RIGHT -> {
                if (!isLose) {
                    moveRight();
                }
            }
            case DOWN -> {
                if (!isLose && letterImageView.getLayoutY() + letterImageView.getFitHeight() + 50 < pane1.getLayoutY()) {
                    moveDown();
                }
            }
            default -> {
            }
        }
    }

    @FXML
    void onPlayAgainClick() {
        velocity = 0;
        isLose = false;
        timeline.play();
        button.setOnMouseClicked(event ->{
            letterList.clear();

            letterImageView.setVisible(false);

            score = 0;
            scoreLabel.setText(String.valueOf(score));

            loadWordsFromFile();
            loadWordExplainFromFile();
            selectRandomWord();
            displayWordExplain();

            for (int i = 0; i < 7; i++) {
                imageViewList.get(i).setVisible(false);
            }

            addLetterInPane();
            loadLevel();

            System.out.println(currentWord);

            for (int i = 0; i < currentWord.length(); i++) {
                letterList.add(currentWord.charAt(i));
            }

            for (Character character : letterList) {
                System.out.print(character);
            }

            System.out.println();

            letterImageView.setVisible(true);

            setImageView();
        });
    }

    public void addPane() {
        panes.add(pane1);
        panes.add(pane2);
        panes.add(pane3);
        panes.add(pane4);
        panes.add(pane5);
        panes.add(pane6);
        panes.add(pane7);
    }

    public void addImageView() {
        imageViewList.add(paneImage1);
        imageViewList.add(paneImage2);
        imageViewList.add(paneImage3);
        imageViewList.add(paneImage4);
        imageViewList.add(paneImage5);
        imageViewList.add(paneImage6);
        imageViewList.add(paneImage7);
    }

    public void addLetterInPane() {
        letterInPane = new ArrayList<>();
        for (int i = 0; i < currentWord.length(); i++) {
            letterInPane.add(null);
        }
    }

    public void loadLevel() {
        isEmptyPane = new ArrayList<>();
        for (Pane p : panes) {
            p.setVisible(false);
        }

        for (int i = 0; i < currentWord.length(); i++) {
            panes.get(i).setVisible(true);
            isEmptyPane.add(true);
        }
    }

    public boolean checkLetterOnPane() {
        return (letterImageView.getLayoutY() + letterImageView.getFitHeight() <= pane1.getLayoutY());
    }

    public void setLetterImageOnPane() {
        switch (posIndex) {
            case 1 -> imageViewList.get(0).setImage(new Image(imagePath));
            case 2 -> imageViewList.get(1).setImage(new Image(imagePath));
            case 3 -> imageViewList.get(2).setImage(new Image(imagePath));
            case 4 -> imageViewList.get(3).setImage(new Image(imagePath));
            case 5 -> imageViewList.get(4).setImage(new Image(imagePath));
            case 6 -> imageViewList.get(5).setImage(new Image(imagePath));
            case 7 -> imageViewList.get(6).setImage(new Image(imagePath));
            default -> {
            }
        }
    }

    public void setImageView() {
        randomIndex = random.nextInt(letterList.size());
        currentLetter = letterList.get(randomIndex);
        imagePath = "D:\\BTL\\Dictionary\\src\\main\\resources\\com\\example\\dictionary\\letterImage\\" + Character.toLowerCase(currentLetter) + ".jpg";
        letterImageView.setImage(new Image(imagePath));
        letterList.remove(randomIndex);

        letterImageView.setLayoutX(getLayoutXPane(posIndex));
        letterImageView.setLayoutY(0);
    }

    public boolean isCorrectWord() {
        StringBuilder word = new StringBuilder();
        for (Character character : letterInPane) {
            if (character == null) return false;
            word.append(character);
        }

        return word.toString().equals(currentWord);
    }

    public boolean createNewWord() {
        for (Boolean aBoolean : isEmptyPane) {
            if (aBoolean) {
                return false;
            }
        }

        return true;
    }

    public void setVisiblePane() {
        for (int i = 0; i < currentWord.length(); i++) {
            imageViewList.get(i).setVisible(false);
        }
    }

    public void setPane() {
        imageViewList.get(posIndex - 1).setVisible(true);
    }

    public void correctWord() {
        score += 10;
        scoreLabel.setText(String.valueOf(score));
        setVisiblePane();
        selectRandomWord();
        displayWordExplain();
        addLetterInPane();
        loadLevel();
        System.out.println(currentWord);

        for (int i = 0; i < currentWord.length(); i++) {
            letterList.add(currentWord.charAt(i));
        }

        int randomIndex = random.nextInt(letterList.size());
        currentLetter = letterList.get(randomIndex);
        imagePath = "D:\\BTL\\Dictionary\\src\\main\\resources\\com\\example\\dictionary\\letterImage\\" + Character.toLowerCase(currentLetter) + ".jpg";
        letterImageView.setImage(new Image(imagePath));

        letterImageView.setLayoutX(getLayoutXPane(1));
        letterImageView.setLayoutY(0);

        System.out.println("correct");
    }

    public void incorrectWord() {
        letterImageView.setVisible(false);
        System.out.println("incorrect");
        button.setManaged(true);
    }
}