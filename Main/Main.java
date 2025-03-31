package Main;

import java.util.Arrays;

import javax.smartcardio.Card;

import Players.Dealer;
import Players.Bots.BasicBot;
import Players.Bots.ExternalPlayer;

public class Main {
    //players and dealers
    private static Dealer dealer = new Dealer();
    private static BasicBot[] Rplayers = new BasicBot[6];
    private static BasicBot[] players;

    //game variables
    private static int BB = 20;
    private static int blindPlayer = 0;
    private static int pot = 0;
    private static String[] table = new String[]{"--", "--", "--", "--", "--"};
    private static String[][] playerCards = new String[6][2];
    private static int highbet = 0;
    private static int[] bets;
    private static int blindMod = BB/2;
    private static int bet = 0;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //main program
    public static void main(String[] args){
        //create players
        Rplayers[0] = new ExternalPlayer();
        Rplayers[1] = new ExternalPlayer();
        Rplayers[2] = null;
        Rplayers[3] = null;
        Rplayers[4] = null;
        Rplayers[5] = null;

        //give players money
        for(int i = 0; i < Rplayers.length; i++){
            if(Rplayers[i] != null){Rplayers[i].giveMoney(2000);}
        }

        //actual gameplay
        while(Rplayers[0] != null || Rplayers[1] != null || Rplayers[2] != null || Rplayers[3] != null || Rplayers[4] != null || Rplayers[5] != null){
            int playercount = 0;
            for(int i = 0; i < Rplayers.length; i++){
                playercount += (Rplayers[i] == null ? 0 : 1);
            }
            if(playercount < 2){break;}

            dealer.shuffle();
            System.out.println("\n\n\nBeginning New Game.....\n\n\n");
            newGame();
            //update players list
            for(int i = 0; i < Rplayers.length; i++){
                if(Rplayers[i] != null && Rplayers[i].getBalance() <= 0){Rplayers[i] = null;}
            }
        }

        System.out.println("Game Over!");
        byte j = 0;
        for(int i = 0; i < Rplayers.length; i++){
            if(Rplayers[i] != null){
                j++;
                System.out.println("Player " + i + " won!");
            }
        }

        if(j == 0){
            System.out.println("No legal players to win. Game Over by Default.");
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //newgame function
    public static void newGame(){
        //reset variables
        blindMod = BB/2;
        pot = 0;
        highbet = 0;
        bets = new int[] {0, 0, 0, 0, 0, 0};
        bet = 0;
        players = Rplayers.clone();

        //deal cards to each player
        for(int i = 0; i < players.length; i++){
            //test if player is null, and continue if so
            if(players[i] == null){continue;}
            System.out.println("Dealing cards to Player " + i);
            playerCards[i] = dealer.deal(2);
            players[i].giveHand(playerCards[i]);
        }

        //blinds
        //default is 10/20
        byte j = 2;
        for(int i = 0; i < j; i++){
            //test if player is null, and continue if so
            if(players[i] == null && j < 6){
                j++;
                continue;
            }else if (players[i] == null && j >= 6){
                throw new IllegalArgumentException("No legal players to blind. Game over.");
            }
            bet = players[(i+blindPlayer)%players.length].blind(BB - blindMod);
            bets[(i+blindPlayer)%players.length] = bet;
            blindMod = 0;
        }
        highbet = BB;
        //initial betting
        if(!bettingRound(false)){showdown();return;}
        highbet = 0;
        updatePot();

        //do the flop and then give the player the flop cards
        table[0] = dealer.deal(1)[0];
        table[1] = dealer.deal(1)[0];
        table[2] = dealer.deal(1)[0];
        System.out.println("Table:" + table[0] + " " + table[1] + " " + table[2] + "\n");
            //give the players the flop
        for(int i = 0; i < players.length; i++){
            //check if player exists
            if (players[i] != null){
                players[i].giveFlop(new String[]{table[0], table[1], table[2]});
            }
        }

        //betting after flop
        if(!bettingRound(false)){showdown();return;}
        highbet = 0;
        updatePot();

        //do the turn and then give the player the turn card
        table[3] = dealer.deal(1)[0];
        System.out.println("Table:" + table[0] + " " + table[1] + " " + table[2] + " " + table[3] + "\n");
            //give the players the turn
        for(int i = 0; i < players.length; i++){
            //check if player exists
            if (players[i] != null){
                players[i].giveTurn(table[3]);
            }
        }

        //betting after turn
        if(!bettingRound(false)){showdown();return;}
        highbet = 0;
        updatePot();

        //do the river and then give the player the river card
        table[4] = dealer.deal(1)[0];
        System.out.println("Table:" + table[0] + " " + table[1] + " " + table[2] + " " + table[3] + " " + table[4] + "\n");
            //give the players the river
        for(int i = 0; i < players.length; i++){
            //check if player exists
            if (players[i] != null){
                players[i].giveRiver(table[4]);
            }
        }

        //betting after river
        if(!bettingRound(false)){showdown();return;}
        updatePot();

        //showdown
        showdown();


    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //betting round function. returns true if the round is to continue, or false if all players but one have folded.
    //if all players fold, then an IllegalArgumentException is thrown. Does not require try catch, because it should never do that
    public static boolean bettingRound(boolean nest){
        for (int botcount = 0; botcount < players.length; botcount++){
            if (players[botcount] != null && !(bets[botcount] == highbet && nest)){
                
                //update the players catelog of bets
                players[botcount].otherBets(bets);

                //now the bot decides its bet, and update the highbet and pot
                bet = players[botcount].bet(highbet - bets[botcount]);
                if (bet > highbet - bets[botcount]){//test if it is a raise
                    highbet = bet + bets[botcount];
                    bets[botcount] += bet;
                    System.out.println("Player " + botcount + " raised to " + highbet + ".\n");
                }else if(bet == highbet - bets[botcount]){//test if it is a call
                    bets[botcount] += bet;
                    System.out.println("Player " + botcount + " called.\n");
                }else if(bet < highbet - bets[botcount] && bet > 0 && players[botcount].getBalance() == 0){//all in
                    bets[botcount] += bet;
                    players[botcount].blind(players[botcount].getBalance());//this force removes all money in the bot's balance
                    System.out.println("Player " + botcount + " went all in.\n");
                }else if(bet == -1){//test if it is a fold, and remove the player
                    players[botcount] = null;
                    playerCards[botcount] = null;
                    bets[botcount] += 0;
                    System.out.println("Player " + botcount + " folded.\n");
                    //test if the round will continue
                    if(testPlayers()){return false;}
                }else{//if the bet is invalid, the player folds
                    players[botcount] = null;
                    playerCards[botcount] = null;
                    bets[botcount] += 0;
                    System.out.println("Player " + botcount + " folded by invalid bet.\n");
                    //test if the round will continue
                    if(testPlayers()){return false;}
                }
            }
        }

        //we need to test if everyon has called, and if not, we will do another betting round
        for(int i = 0; i < bets.length; i++){
            if(bets[i] < highbet && players[i] != null){
                bettingRound(true);
                break;
            }
        }

        //double check that there are more than one players left
        int bottotal = 0;
        for(BasicBot player: players){
            if(player != null){
                bottotal++;
            }
        }
        //returns false if all other players fold but one
        if(bottotal == 1){
            return false;
        }else if(bottotal > 1){
            return true;
        }else{
            throw new IllegalArgumentException();

        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //function to update the pot
    public static void updatePot(){
        //do this for each bet
        for(int i = 0; i < bets.length; i++){
            //make sure the bet exists
            if(bets[i] > 0){
                pot+=bets[i];
                bets[i] = 0;
            }
        }
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//function to test number of players
//returns false if there are more than one player, and true if there is only one
    public static boolean testPlayers(){
        byte activePlayers = 0;
        for(int i = 0; i < players.length; i++){
            if (players[i] != null){
                activePlayers++;
            }
        }

        if(activePlayers > 1){
            return false;
        }else if (activePlayers == 1){
            return true;
        }else{
            throw new IndexOutOfBoundsException("Not enough players to continue.");
        }
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //function to do a showdown

    public static void showdown(){
        int[] handStrength = new int[players.length];
        //get each players hand strength
        for(int i = 0; i < players.length; i++){
            if (players[i] != null){
                handStrength[i] = evalHand(playerCards[i]);
                //DEBUG
                System.out.println("Player " + i + " has a hand strength of " + handStrength[i]);
            }
        }

        //find the highest hand strength
        int max = handStrength[0];
        int[] maxindex = new int[handStrength.length];
        int maxes = 0;
        for(int i = 1; i < handStrength.length; i++){
            if(handStrength[i] > max){
                max = handStrength[i];
                maxindex[maxes] = i;
                for(int j = 0; j < maxes; j++){
                    maxindex[j] = 0;
                }
            }else if(handStrength[i] == max){
                maxes++;
                maxindex[maxes] = i;
            }
        }

        //distribute the pot to the winners
        for(int i = 0; i < maxindex.length; i++){
            if((maxindex[i] != 0 && i != 0) || (maxindex[i] == 0 && i == 0)){
                players[maxindex[i]].giveMoney(pot/(maxes + 1));
                System.out.println("Player " + maxindex[i] + " Won " + pot/(maxes + 1));
            }
        }
        
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //this will find the strength of each hand, and the strength of the variation of the hand.
    public static int evalHand(String[] hand){
        /*
         *We can reduce each hand to a few types. flush, stright, straight flush, and ___ of a kind / full house.
         *We can modify a value to fit each of these and store how many of each kind are in the hand.
         *Then, we evaluate the greatest part of the hand, frome strong to weak
         *and calculate that into a strength value, rather than a type of hand to compare later
         *
         * This value will be a 5 digit number. The first digit is the ranking of the hand type; eg. high = 0, pair = 1, etc.
         * The next two digits are the value within that type; eg. a pair of A is 13, and a pair of 6 is 05.
         * The final two digit is the kicker if used; eg. a kicker of 6 would be 05, 7 would be 06, etc
         * So, a pair of 6 kicker A would be 10513
         */

        //first we setup our test variables
        String[] allCards = new String[]{hand[0], hand[1], table[0], table[1], table[2], table[3], table[4]};
        String[] flush = new String[]{"--", "--", "--", "--", "--", "--", "--"};
        byte[] pairs = new byte[]{0, 0, 0, 0};
        byte straightHigh = 0;
        char flushSuit = '-';
        byte[] trips = new byte[]{0, 0};
        byte quads = 0;
        int evaluation = 00000;

        //we need to do this in steps. First, we check for a flush

        byte hearts = 0;
        byte spades = 0;
        byte clubs = 0;
        byte diamonds = 0;
        for(int i = 0; i < allCards.length; i++){
            switch(allCards[i].charAt(1)){
                case 'h': 
                    hearts++;
                    break;
                case 's': 
                    spades++;
                    break;
                case 'c': 
                    clubs++;
                    break;
                case 'd': 
                    diamonds++;
                    break;
                default:
                    break;
            }
        }

        //now tell the evaluator what the flush suit is
        if(hearts == 5){
            flushSuit = 'h';
        }else if(spades == 5){
            flushSuit = 's';
        }else if(clubs == 5){
            flushSuit = 'c';
        }else if(diamonds == 5){
            flushSuit = 'd';
        }

        //get all the cards in the flush
        for(int i = 0; i < allCards.length; i++){
            if(allCards[i].charAt(1) == flushSuit){
                flush[i] = allCards[i];
            }
        }

        //next straights

        //cardvals will be used severall times later on
        byte[] cardVals = new byte[7];

        //convert card values to bytes
        for(int i = 0; i < allCards.length; i++){
            switch(allCards[i].charAt(0)){
                case '2': cardVals[i] = 2; break;
                case '3': cardVals[i] = 3; break;
                case '4': cardVals[i] = 4; break;
                case '5': cardVals[i] = 5; break;
                case '6': cardVals[i] = 6; break;
                case '7': cardVals[i] = 7; break;
                case '8': cardVals[i] = 8; break;
                case '9': cardVals[i] = 9; break;
                case 'T': cardVals[i] = 10; break;
                case 'J': cardVals[i] = 11; break;
                case 'Q': cardVals[i] = 12; break;
                case 'K': cardVals[i] = 13; break;
                case 'A': cardVals[i] = 14; break;
            }
        }

        Arrays.sort(cardVals);

        int straightLen = 1;

        //checks for a straight
        //DEBUG
        for(int i = 1; i < cardVals.length; i++) {
            if(cardVals[i-1] + 1 == cardVals[i] && straightLen < 5){
                straightLen++;
                straightHigh = cardVals[i];
            }else if(cardVals[i-1] == cardVals[i] && straightLen < 5){
                //do nothing
            }else if(straightLen < 5){
                straightLen = 1;
                straightHigh = 0;
            }

        }

        //var to use for all sets
        byte numcard = 0;

        //next quads, trips, and pairs
        for(int i = 0; i < cardVals.length; i++){
            for(int j=0; j < cardVals.length; j++){
                if(cardVals[i] == cardVals[j]){
                    numcard++;
                }
            }
            //check if there are 4, 3, or 2
            if(numcard == 4){
                //DEBUG
                //System.out.println("Found a quad" + cardVals[i]);
                quads = cardVals[i];
                break;
            }else if(numcard == 3 && trips[0] == 0){
                //DEBUG
                //System.out.println("Found a trip" + cardVals[i]);
                trips[0] = cardVals[i];
            }else if(numcard == 3 && trips[0] > 0 && trips[0] != cardVals[i]){
                //DEBUG
                //System.out.println("Found another trip" + cardVals[i]);
                trips[1] = cardVals[i];
                break;
            }else if(numcard == 2 && pairs[0] == 0){
                //DEBUG
                //System.out.println("Found a pair" + cardVals[i]);
                pairs[0] = cardVals[i];
            }else if(numcard == 2 && pairs[0] > 0 && pairs[1] == 0 && cardVals[i] != pairs[0]){
                //DEBUG
                //System.out.println("Found another pair" + cardVals[i]);
                pairs[1] = cardVals[i];
            }else if(numcard == 2 && pairs[1] > 0 && pairs[2] == 0 && cardVals[i] != pairs[1]){
                //DEBUG
                //System.out.println("Found another pair" + cardVals[i]);
                pairs[2] = cardVals[i];
            }else if(numcard == 2 && pairs[2] > 0 && pairs[3] == 0 && cardVals[i] != pairs[2]){
                //DEBUG
                //System.out.println("Found another pair" + cardVals[i]);
                pairs[3] = cardVals[i];
                break;
            }
            //reset numcard
            numcard = 0;
        }


        //now we need to compile the results into a result number

        //starting with straight flushes
        if(straightHigh != 0 && flushSuit != '-'){

            byte[] flushCardVals = new byte[7];

            //firts, get the cards in the flush

            for(int i = 0; i < flush.length; i++){
                switch(flush[i].charAt(0)){
                    case '2': flushCardVals[i] = 2; break;
                    case '3': flushCardVals[i] = 3; break;
                    case '4': flushCardVals[i] = 4; break;
                    case '5': flushCardVals[i] = 5; break;
                    case '6': flushCardVals[i] = 6; break;
                    case '7': flushCardVals[i] = 7; break;
                    case '8': flushCardVals[i] = 8; break;
                    case '9': flushCardVals[i] = 9; break;
                    case 'T': flushCardVals[i] = 10; break;
                    case 'J': flushCardVals[i] = 11; break;
                    case 'Q': flushCardVals[i] = 12; break;
                    case 'K': flushCardVals[i] = 13; break;
                    case 'A': flushCardVals[i] = 14; break;
                }
            }

            Arrays.sort(flushCardVals);

            byte fstraithigh = 0;

            //now we check for the flush straight
            for(int i = 1; i < flushCardVals.length; i++){
                
                if(flushCardVals[i - 1] + 1 == flushCardVals[i] && straightLen < 5){
                    straightLen++;
                    fstraithigh = cardVals[i];
                }else if(straightLen < 5){
                    straightLen = 0;
                    fstraithigh = 0;
                }
    
            
                
            }

            if(fstraithigh != 0){
                evaluation = 90000 + ((fstraithigh - 1) * 100);
                return evaluation;
            }
        }

        //now we check for quads
        if(quads != 0){
            for(int i = cardVals.length - 1; i >= 0; i--){
                if(cardVals[i] != quads){
                    evaluation = 80000 + (quads * 100) + (cardVals[i]);
                    break;
                }
            }
            return evaluation;
        }

        //now we check for full house
        if(trips[0] != 0 && pairs[0] != 0 && trips[0] != pairs[0]){
            int highTrip = 0;
            int highPair = 0;
            //get the highest set
            for(int i = 0; i < trips.length; i++){
                highTrip = highTrip > trips[i] ? highTrip : trips[i];
            }
            for(int i = 0; i < trips.length; i++){
                highPair = highPair > pairs[i] ? highPair : pairs[i];
            }

            //do the eval
            evaluation = 70000 + (highTrip * 100) + highPair;
            return evaluation;
        }

        //now we check for flush
        if(flushSuit != '-'){

            // get the highest card in the flush, then second high
            int high = 0;
            int secondHigh = 0;
            for(int i = 0; i < flush.length; i++){
                if(flush[i] != "--"){
                    //get the flush card value
                    switch(flush[i].charAt(0)){
                        case '2': high = high < 1 ? 1 : high; break;
                        case '3': high = high < 2 ? 2 : high; break;
                        case '4': high = high < 3 ? 3 : high; break;
                        case '5': high = high < 4 ? 4 : high; break;
                        case '6': high = high < 5 ? 5 : high; break;
                        case '7': high = high < 6 ? 6 : high; break;
                        case '8': high = high < 7 ? 7 : high; break;
                        case '9': high = high < 8 ? 8 : high; break;
                        case 'T': high = high < 9 ? 9 : high; break;
                        case 'J': high = high < 10 ? 10 : high; break;
                        case 'Q': high = high < 11 ? 11 : high; break;
                        case 'K': high = high < 12 ? 12 : high; break;
                        case 'A': high = high < 13 ? 13 : high; break;
                    }

                    //get the second highest flush card value
                    if(i != 0){
                        switch(flush[i - 1].charAt(0)){
                            case '2': secondHigh = secondHigh < 1 && high > 1 ? 1 : secondHigh; break;
                            case '3': secondHigh = secondHigh < 2 && high > 2 ? 2 : secondHigh; break;
                            case '4': secondHigh = secondHigh < 3 && high > 3 ? 3 : secondHigh; break;
                            case '5': secondHigh = secondHigh < 4 && high > 4 ? 4 : secondHigh; break;
                            case '6': secondHigh = secondHigh < 5 && high > 5 ? 5 : secondHigh; break;
                            case '7': secondHigh = secondHigh < 6 && high > 6 ? 6 : secondHigh; break;
                            case '8': secondHigh = secondHigh < 7 && high > 7 ? 7 : secondHigh; break;
                            case '9': secondHigh = secondHigh < 8 && high > 8 ? 8 : secondHigh; break;
                            case 'T': secondHigh = secondHigh < 9 && high > 9 ? 9 : secondHigh; break;
                            case 'J': secondHigh = secondHigh < 10 && high > 10 ? 10 : secondHigh; break;
                            case 'Q': secondHigh = secondHigh < 11 && high > 11 ? 11 : secondHigh; break;
                            case 'K': secondHigh = secondHigh < 12 && high > 12 ? 12 : secondHigh; break;
                            case 'A': secondHigh = secondHigh < 13 && high > 13 ? 13 : secondHigh; break;
                        }
                    }
                }
            }

            evaluation = 60000 + (high * 100) + secondHigh;
            return evaluation;
        }

        //now we check for straight
        if(straightLen == 5){
            evaluation = 50000 + (straightHigh * 100);
            return evaluation;
        }

        //now 3 of a kind
        if(trips[0] != 0){
            byte t = 0;
            byte h = 0;
            //get highest trip
            for(int i = 0; i < trips.length; i++){
                t = t > trips[i] ? t : trips[i];
            }
            //get kicker
            for(int i = 0; i < cardVals.length; i++){
                h = (cardVals[i] == t) || (cardVals[i] <= h) ? h : cardVals[i];
            }
            //eval time
            evaluation = 40000 + (t * 100) + h;
            return evaluation;
        }

        //now two pair
        if(pairs[0] != 0 && pairs[1] != 0 && pairs[0] != pairs[1]){
            byte h = 0;
            byte s = 0;
            //get highest pair
            for(int i = pairs.length - 1; i >= 0; i--){
                h = h >= pairs[i] ? h : pairs[i];
            }
            //get second highest pair
            for(int i = pairs.length - 1; i >= 0; i--){
                s = s >= pairs[i] || pairs[i] >= h ? s : pairs[i];
            }

            //eval time
            evaluation = 30000 + (h * 100) + s;
            return evaluation;
        }

        //finally one pair
        if(pairs[0] != 0 && pairs[1] == 0){
            byte k = 0;
            //get kicker
            for(int i = cardVals.length - 1; i > 0; i--){
                k = (cardVals[i] != pairs[0]) && (cardVals[i] > k) ? cardVals[i] : k;
            }
            //evaluation
            evaluation = 20000 + (pairs[0] * 100) + k;
        }

        //the final one! high card
        if(evaluation == 0){
            evaluation = 10000 + cardVals[cardVals.length - 1];
        }


        //return the result
        return evaluation;

        //DONE!!!
    }

}
