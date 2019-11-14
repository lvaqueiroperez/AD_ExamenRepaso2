package repasexamen2;

public class Platos {

    private String codp;
    private String nombre;
    private int grasaTotal;

    public Platos() {
    }

    public Platos(String codp, String nombre, int grasaTotal) {
        this.codp = codp;
        this.nombre = nombre;
        this.grasaTotal = grasaTotal;
    }

    public String getCodp() {
        return codp;
    }

    public String getNombre() {
        return nombre;
    }

    public int getGrasaTotal() {
        return grasaTotal;
    }

    public void setCodp(String codp) {
        this.codp = codp;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setGrasaTotal(int grasaTotal) {
        this.grasaTotal = grasaTotal;
    }

    @Override
    public String toString() {
        return "Platos{" + "codp=" + codp + ", nombre=" + nombre + ", grasaTotal=" + grasaTotal + '}';
    }

}
