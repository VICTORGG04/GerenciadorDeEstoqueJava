package service;

import model.Produto;
import repository.ProdutoRepository;
import java.util.List;

public class EstoqueService {
    private ProdutoRepository repository = new ProdutoRepository();
    private List<Produto> produtos;

    public EstoqueService() {
        produtos = repository.listar();
    }

    public List<Produto> listar() { return produtos; }

    public void adicionar(Produto p) {
        produtos.add(p);
        repository.salvar(produtos);
    }

    public void remover(Produto p) {
        produtos.remove(p);
        repository.salvar(produtos);
    }

    public void atualizar(Produto antigo, Produto novo) {
        int index = produtos.indexOf(antigo);
        if (index != -1) {
            produtos.set(index, novo);
            repository.salvar(produtos);
        }
    }

    // --- MÉTODOS DA VERSÃO 2.0 ---

    public double calcularValorTotalCusto() {
        return produtos.stream().mapToDouble(p -> p.getPreco() * p.getQuantidade()).sum();
    }

    public double calcularPatrimonioVenda() {
        return produtos.stream().mapToDouble(p -> p.getPrecoVenda() * p.getQuantidade()).sum();
    }

    public int contarTotalItens() {
        return produtos.stream().mapToInt(Produto::getQuantidade).sum();
    }

    public boolean darBaixa(String busca) {
        for (Produto p : produtos) {
            if (p.getNome().equalsIgnoreCase(busca.trim()) && p.getQuantidade() > 0) {
                p.setQuantidade(p.getQuantidade() - 1);
                repository.salvar(produtos);
                return true;
            }
        }
        return false;
    }
}