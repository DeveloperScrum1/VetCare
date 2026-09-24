package vetcare.gestion;

public interface IGestionable<T> {
    void registrar(T elemento);
    T buscar(int id);
    void listar();
}
