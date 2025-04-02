package Players.Bots;

import java.util.Scanner;

public class ExternalPlayer extends BasicBot{
    //variables for keeping track of player stuff
    private String[] cards = new String[2];
    private int balance = 0;
    private String[] table = new String[5];
    private int[] bets = new int[] {-2, -2, -2, -2, -2, -2};
    private Scanner input = new Scanner(System.in);


    //betting method
    @Override
    public int bet(int call) {
        //get input for the bet
        System.out.println("Enter your bet (C to see all cards, B to see other players bets, and M to see your balance)\n-1 to fold, or " + call + " to call:  ");
        String bet = input.nextLine();
        int betAmount = 0;
        //convert the input to an integer, or give the requested data, then sets betAmount to bet(int call) through recursion.
        // This will allow a near infinite amount of rereading information
        switch(bet){
            case "C":
                System.out.println(cards[0] + " " + cards[1] + " " + table[0] + " " + table[1] + " " + table[2] + " " + table[3] + " " + table[4]);
                betAmount = bet(call);
                break;
            case "B":   
                System.out.println(bets[0] + " " + bets[1] + " " + bets[2] + " " + bets[3] + " " + bets[4] + " " + bets[5]);
                betAmount = bet(call);
                break;
            case "M":
                System.out.println(balance);
                betAmount = bet(call);
                break;
            default:
                try{
                    betAmount = Integer.parseInt(bet);
                    break;
                }catch(Exception e){
                    System.out.println("Invalid input.");
                    betAmount = bet(call);
                    break;
                }
            
        }
        //check the validity of the bet
        if((betAmount >= call && betAmount <= balance) || betAmount == balance || betAmount == -1){
            //do nothing
        }else if(betAmount < call){
            System.out.println("You must bet at least " + call + " or your balance.");
            betAmount = bet(call);
        }else if(betAmount > balance){
            System.out.println("Insufficient Funds. Please enter a smaller amount.");
            betAmount = bet(call);
        }
        if(betAmount == balance){
            System.out.println("Out of funds. ALL IN!");
        }
        balance -= betAmount;
        return betAmount;
    }

    @Override
    public String[] getCards() {
        // returns the players cards
        return cards;
        
    }

    @Override
    public void giveHand(String[] hand) {
        // create the players hand
        cards = hand;
        
    }

    @Override
    public void givePlayers(int numPlayers) {
        // Auto-generated method stub may not be needed. TBD
        
    }

    @Override
    public void givePlayerCards(String[][] otherCards, int thisPlayer) {
        System.out.println();
        for(int i = 0; i < otherCards.length; i++){
            if(i == thisPlayer){continue;}
            System.out.println("Player " + i + " had: " + otherCards[i][0] + " " + otherCards[i][1]);
        }
    }

    @Override
    public void giveFlop(String[] flop) {
        // give the player the flop
        table[0] = flop[0];
        table[1] = flop[1];
        table[2] = flop[2];
        
    }

    @Override
    public void giveTurn(String turn) {
        // give the player the turn
        table[3] = turn;
        
    }

    @Override
    public void giveRiver(String river) {
        // give the player the river
        table[4] = river;
        
    }

    @Override
    public void otherBets(int[] bets) {
        // gives this player what other players have bet
        this.bets = bets;
        
    }

    @Override
    public void giveMoney(int money) {
        // adds money to the players balance
        balance += money;
        
    }

    @Override
    public int blind(int blindAmount) {
        // forces the blind
        if(balance >= blindAmount){
            balance -= blindAmount;
            return blindAmount;
        }else{
            System.out.println("Out of funds. ALL IN!");
            return blind(blindAmount);
        }
        
    }

    @Override
    public int getBalance() {
        // returns the players balance
        return balance;
    }
}