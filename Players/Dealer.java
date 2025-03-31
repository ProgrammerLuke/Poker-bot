package Players;
//import statements
import java.util.ArrayList;
import java.util.Collections;

public class Dealer {

    //variables to be used in the rest of the code
    private ArrayList<String> cards = new ArrayList<String>();
    private char[] suits = {'c', 'd', 'h', 's'};
    private char[] values = {'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};

    //basic constructor. Use to get cards
    public Dealer(){
        getCards();
    }
    //This is the primary method of the dealer. This function will deal the cards
    public String[] deal(int numCards){
        String[] hand = new String[numCards];
        for(int i = 0; i < numCards; i++){
            hand[i] = cards.remove(cards.size() - 1);
        }
        return hand;
    }

    //method to shuffle the cards
    public void shuffle(){
        Collections.shuffle(cards);
        //stacking the deck for testing happens down here
        // cards = new ArrayList<String>();
        // cards.add(0, "As");
        // cards.add(0, "Ah");
        // cards.add(0, "Kc");
        // cards.add(0, "Qh");
        // cards.add(0, "3d");
        // cards.add(0, "8s");
        // cards.add(0, "8c");
        // cards.add(0, "4d");
        // cards.add(0, "2s");
    }

    //method to get the cards
    private void getCards(){
        for(char s : suits){
            for(char v : values){
                cards.add("" + v + s);
            }
        }
    }
}
