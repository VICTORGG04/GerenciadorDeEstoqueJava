package repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Produto;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class ProdutoRepository {

    private final String FILE_PATH = "produtos.json";
    private final Gson gson = new Gson();

    public List<Produto> listar() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<List<Produto>>(){}.getType();
            List<Produto> produtos = gson.fromJson(reader, listType);
            return produtos != null ? produtos : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void salvar(List<Produto> produtos) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(produtos, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}