package ca.mcmaster.se2aa4.island.team210.DecisionMaker;

import ca.mcmaster.se2aa4.island.team210.Decision;
import ca.mcmaster.se2aa4.island.team210.DecisionMaker.DecisionGenerator;
import ca.mcmaster.se2aa4.island.team210.Map;

import java.util.*;

public class DecisionGeneratorIsland implements DecisionGenerator {

    enum state{
        START,
        SEARCH,
        NAVIGATE,
        LOOPING,
        DONE,
    }
    private state current_state;

    Queue<Decision> decQueue;

    public DecisionGeneratorIsland(){

        current_state = state.START;
        decQueue = new LinkedList<>();
    }


    public Decision decidingAlgorithm(ca.mcmaster.se2aa4.island.team210.Map givenMap){
        if (!decQueue.isEmpty()){
            return decQueue.remove();
        }
        switch (current_state){
            case START:
                decQueue.add(new Decision("echo", givenMap.getDirection()));
                decQueue.add(new Decision("echo", givenMap.getRight()));
                decQueue.add(new Decision("echo", givenMap.getLeft()));
                canSwitchStates(givenMap);
                break;
            case SEARCH:
                canSwitchStates(givenMap);
                if (current_state.equals(state.NAVIGATE)){
                    decQueue.add(new Decision("scan"));
                }
                else {
                    decQueue.add(new Decision("fly"));
                    decQueue.add(new Decision("echo", givenMap.getLeft()));
                    decQueue.add(new Decision("echo", givenMap.getRight()));
                }
                break;
            case NAVIGATE:
                if (givenMap.getEchoType("left").equals("GROUND")) {
                    decQueue.add(new Decision("heading", givenMap.getLeft()));
                    givenMap.setDroneStartingTurn("left");

                    for (int i = 0; i < givenMap.getRange("left") - 1; i++){
                        decQueue.add(new Decision("fly"));
                    }
                }
                else if (givenMap.getEchoType("right").equals("GROUND")){
                    decQueue.add(new Decision("heading", givenMap.getRight()));
                    givenMap.setDroneStartingTurn("right");

                    for (int i = 0; i < givenMap.getRange("right") - 1; i++){
                        decQueue.add(new Decision("fly"));
                    }
                }
                else{
                    for (int i = 0; i < givenMap.getRange("current"); i++){
                        decQueue.add(new Decision("fly"));
                    }
                }

                decQueue.add(new Decision("scan"));
                canSwitchStates(givenMap);
                break;
            case LOOPING:
                canSwitchStates(givenMap);
                decQueue.add(new Decision("scan"));
                break;
            case DONE:
                break;
        }
        return decQueue.remove();

    }

    private void canSwitchStates(Map givenMap) {
        switch (current_state){
            case START:
                if (givenMap.getEchoType("current").equals("OUT_OF_RANGE")) {
                    switchStates(state.NAVIGATE);
                }
                else if (givenMap.getEchoType("right").equals("OUT_OF_RANGE")) {
                    switchStates(state.NAVIGATE);
                }
                else if (givenMap.getEchoType("left").equals("OUT_OF_RANGE")) {
                    switchStates(state.NAVIGATE);
                }
                else{
                    switchStates(state.SEARCH);
                }
                break;
            case SEARCH:
                if (!givenMap.getEchoType("right").equals("OUT_OF_RANGE")) {
                    switchStates(state.NAVIGATE);
                    decQueue.clear();
                }
                else if (!givenMap.getEchoType("left").equals("OUT_OF_RANGE")) {
                    switchStates(state.NAVIGATE);
                    decQueue.clear();
                }
                break;
            case NAVIGATE:
                switchStates(state.LOOPING);
                break;
            case LOOPING:
                if (decQueue.isEmpty()) {
                    switchStates(state.DONE);
                }
            case DONE:
                break;
        }
    }

    private void switchStates(state s){
        current_state = s;
    }

    public String getState(){
        return current_state.toString();
    }
}