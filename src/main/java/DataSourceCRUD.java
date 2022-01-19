public interface DataSourceCRUD<T> {
    //CRUD - create, read, update, delete
    public T create(T t);
    public T read(Integer id);
    public T update(T t);
    public void delete(Integer id);
}
