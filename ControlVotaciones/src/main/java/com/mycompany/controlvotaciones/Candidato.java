
package com.mycompany.controlvotaciones;

public class Candidato {
    private int id;
    private String fullName;
    private String grade;
    private String gender;
    private int votos;

    public Candidato(String fullName, String grade, String gender) {
        this.fullName = fullName.trim();
        this.grade = grade;
        this.gender = gender;
        this.votos = 0;
    }

    public Candidato(int id, String fullName, String grade, String gender, int votos) {
        this.id = id;
        this.fullName = fullName;
        this.grade = grade;
        this.gender = gender;
        this.votos = votos;
    }

    // Getters y Setters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getGrade() { return grade; }
    public String getGender() { return gender; }
    public int getVotos() { return votos; }

    public void agregarVotos(int cantidad) { this.votos += cantidad; }

    @Override
    public String toString() {
        return fullName + " (" + grade + ") - " + votos + " votos";
    }
}