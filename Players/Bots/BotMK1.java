package Players.Bots;

public class BotMK1 extends BasicBot{

    //variables
    private int balance = 0;
    private String[] allCards = new String[7];
    private int numPlayers = 0;
    private int[] bets = new int[] {-2, -2, -2, -2, -2, -2};

    //simply gives the player the number of players
    @Override
    public void givePlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    //give the player his hand. Always expects a 2 card hand, otherwise throws an error
    @Override
    public void giveHand(String[] hand) {
        if(hand.length == 2){
            //put the hand in the available cards
            allCards[0] = hand[0];
            allCards[1] = hand[1];
        }else{
            //throw error if hand is not 2
            throw new IllegalArgumentException("Expected 2 cards in hand, received " + hand.length);
        }
        
    }
    @Override
    public void givePlayerCards(String[][] otherCards, int thisPlayer) {
        // TODO
        
    }

    //add the flop to the available cards. Always expects 3 cards
    @Override
    public void giveFlop(String[] flop) {
        if(flop.length == 3){
            //put the flop in the available cards
            allCards[2] = flop[0];
            allCards[3] = flop[1];
            allCards[4] = flop[2];
        }else{
            //throw error if flop is not 3
            throw new IllegalArgumentException("Expected 3 cards in flop, received " + flop.length);
        }
        
    }

    //add the turn to the available cards. Always expects 1 card
    @Override
    public void giveTurn(String turn) {
        if(turn.length() == 1){
            allCards[5] = turn;
        }else{
            //throw error if turn is not 1
            throw new IllegalArgumentException("Expected 1 card in turn, received " + turn.length());
        }
        
    }

    //add the river to the available cards. Always expects 1 card
    @Override
    public void giveRiver(String river) {
        if(river.length() == 1){
            allCards[5] = river;
        }else{
            //throw error if turn is not 1
            throw new IllegalArgumentException("Expected 1 card in river, received " + river.length());
        }
        
    }

    //gives the player the other players bets
    @Override
    public void otherBets(int[] bets) {
        this.bets = bets;
        
    }

    //give the player more money
    @Override
    public void giveMoney(int money) {
        balance += money;
        
    }
    @Override
    public int bet(int call) {
        // TODO
        return 0;
    }

    //must blind the full amount if able. Otherwise you can go all in
    @Override
    public int blind(int blindAmount) {
        if(blindAmount <= balance){
            balance -= blindAmount;
            return blindAmount;
        }else{
            return balance;
        }
    }

    //returns the players balance
    @Override
    public int getBalance() {
        return balance;
    }

    //returns the players hand
    @Override
    public String[] getCards() {
        return new String[]{allCards[0], allCards[1]};
    }
}
