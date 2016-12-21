package com.mrozwadowski.checkers.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a move, possibly composed of more than one moves.
 * Created by rozwad on 20.12.16.
 */
public class MoveSequence {
    private List<Field> fields;
    private List<Pawn> captures;

    MoveSequence(Field source) {
        fields = new ArrayList<>();
        captures = new ArrayList<>();
        fields.add(source);
    }

    MoveSequence(Field source, Field target) {
        fields = new ArrayList<>();
        captures = new ArrayList<>();
        fields.add(source);
        fields.add(target);
    }

    MoveSequence(MoveSequence initial, Field last) {
        fields = new ArrayList<>(initial.fields.size() + 1);
        fields.addAll(initial.fields);
        fields.add(last);

        captures = new ArrayList<>(initial.captures.size() + 1);
        captures.addAll(initial.captures);
    }

    public Field getSource() {
        return fields.get(0);
    }

    public Field getTarget() {
        return fields.get(fields.size()-1);
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<Pawn> getCaptures() {
        return captures;
    }

    void addCapture(Pawn pawn) {
        captures.add(pawn);
    }

    @Override
    public String toString() {
        String out = "";
        for (Field field: fields) {
            out += field.userFriendlyCoordinates() + " ";
        }
        return out;
    }
}
