package ui;

import javafx.application.Application;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Produto;
import service.EstoqueService;
import java.net.URL;

public class Main extends Application {

    private EstoqueService service = new EstoqueService();
    private ObservableList<Produto> produtos;
    private ObservableList<String> categorias = FXCollections.observableArrayList("Maquilhagem", "Perfumes", "Corpo", "Cabelos", "Casa");

    private Label lblTotalItens = new Label("0");
    private Label lblValorCusto = new Label("R$ 0,00");
    private Label lblPatrimonioVenda = new Label("R$ 0,00");
    private PieChart graficoFinanceiro = new PieChart();

    @Override
    public void start(Stage stage) {
        produtos = FXCollections.observableArrayList(service.listar());
        FilteredList<Produto> listaFiltrada = new FilteredList<>(produtos, p -> true);

        TableView<Produto> tabela = new TableView<>(listaFiltrada);
        configurarColunas(tabela);
        tabela.setPrefHeight(380); // Altura controlada para não empurrar os botões

        // CONFIGURAÇÃO DO GRÁFICO (Otimizado para Tooltips e Espaço)
        graficoFinanceiro.setLabelsVisible(false);
        graficoFinanceiro.setLegendSide(Side.BOTTOM);
        graficoFinanceiro.setMinSize(400, 400);
        graficoFinanceiro.setPrefSize(400, 400);

        Runnable atualizarDashboard = () -> {
            lblTotalItens.setText(String.valueOf(service.contarTotalItens()));
            lblValorCusto.setText(String.format("R$ %.2f", service.calcularValorTotalCusto()));
            lblPatrimonioVenda.setText(String.format("R$ %.2f", service.calcularPatrimonioVenda()));

            ObservableList<PieChart.Data> dados = FXCollections.observableArrayList();
            for (Produto p : produtos) {
                if (p.getPrecoVenda() > 0 && p.getQuantidade() > 0) {
                    dados.add(new PieChart.Data(p.getNome(), p.getPrecoVenda() * p.getQuantidade()));
                }
            }
            graficoFinanceiro.setData(dados);

            // INSTALA TOOLTIPS NAS FATIAS DO GRÁFICO
            graficoFinanceiro.getData().forEach(data -> {
                Tooltip t = new Tooltip(String.format("%s: R$ %.2f", data.getName(), data.getPieValue()));
                t.setShowDelay(Duration.millis(150));
                Tooltip.install(data.getNode(), t);

                data.getNode().setOnMouseEntered(e -> data.getNode().setStyle("-fx-opacity: 0.8; -fx-cursor: hand;"));
                data.getNode().setOnMouseExited(e -> data.getNode().setStyle("-fx-opacity: 1.0;"));
            });
        };

        // PESQUISA E LEITOR
        TextField txtBusca = new TextField();
        txtBusca.setPromptText("🔍 Pesquisar por nome ou código...");
        configurarTooltip(txtBusca, "Filtre os produtos instantaneamente.");
        txtBusca.textProperty().addListener((o, vA, vN) -> {
            listaFiltrada.setPredicate(p -> {
                if (vN == null || vN.isEmpty()) return true;
                String low = vN.toLowerCase();
                return p.getNome().toLowerCase().contains(low) || p.getCodigo().toLowerCase().contains(low);
            });
        });

        TextField txtBaixa = new TextField();
        txtBaixa.setPromptText("Baixa Rápida...");
        configurarTooltip(txtBaixa, "Digite o nome/código e aperte Enter para baixar 1 unidade.");
        txtBaixa.setOnAction(e -> {
            if (service.darBaixa(txtBaixa.getText())) {
                txtBaixa.clear(); atualizarDashboard.run(); tabela.refresh();
            }
        });

        // BOTÕES DE AÇÃO
        Button btnEditar = new Button("Editar Selecionado");
        configurarTooltip(btnEditar, "Alterar dados do produto selecionado.");
        btnEditar.setOnAction(e -> {
            Produto sel = tabela.getSelectionModel().getSelectedItem();
            if (sel != null) abrirJanelaEdicao(sel, atualizarDashboard);
        });

        Button btnRemover = new Button("Remover Selecionado");
        btnRemover.getStyleClass().add("button-remover");
        configurarTooltip(btnRemover, "Excluir o item permanentemente do sistema.");
        btnRemover.setOnAction(e -> {
            Produto sel = tabela.getSelectionModel().getSelectedItem();
            if (sel != null) { service.remover(sel); produtos.remove(sel); atualizarDashboard.run(); }
        });

        // FORMULÁRIO DE CADASTRO (Com nova coluna Código)
        TextField tfCod = new TextField(); tfCod.setPromptText("Cód"); tfCod.setPrefWidth(70);
        TextField tfN = new TextField(); tfN.setPromptText("Nome Produto"); tfN.setPrefWidth(140);
        TextField tfQ = new TextField(); tfQ.setPromptText("Qtd"); tfQ.setPrefWidth(50);
        TextField tfC = new TextField(); tfC.setPromptText("Custo"); tfC.setPrefWidth(70);
        TextField tfV = new TextField(); tfV.setPromptText("Venda"); tfV.setPrefWidth(70);
        ComboBox<String> cbC = new ComboBox<>(categorias); cbC.setPromptText("Categoria"); cbC.setPrefWidth(120);

        Button btnAdd = new Button("Add");
        configurarTooltip(btnAdd, "Cadastrar novo produto no estoque.");
        btnAdd.setOnAction(e -> {
            try {
                Produto p = new Produto(tfCod.getText(), tfN.getText(), Integer.parseInt(tfQ.getText()), Double.parseDouble(tfC.getText()), cbC.getValue());
                p.setPrecoVenda(Double.parseDouble(tfV.getText()));
                service.adicionar(p); produtos.add(p); atualizarDashboard.run();
                tfCod.clear(); tfN.clear(); tfQ.clear(); tfC.clear(); tfV.clear();
            } catch (Exception ex) {
                System.out.println("Erro ao validar campos.");
            }
        });

        // LAYOUT DASHBOARD
        HBox cardBox = new HBox(15, criarCard("Unidades", lblTotalItens, "card-padrao"),
                criarCard("Total Custo", lblValorCusto, "card-valor-verde"),
                criarCard("Total Venda", lblPatrimonioVenda, "card-valor-verde"));
        cardBox.setAlignment(Pos.CENTER);

        // COMPOSIÇÃO FINAL
        HBox barraSuperior = new HBox(10, new Label("Pesquisar:"), txtBusca, new Label("Leitor:"), txtBaixa);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);

        VBox layoutEsquerdo = new VBox(15, barraSuperior, tabela,
                new HBox(8, tfCod, tfN, tfQ, tfC, tfV, cbC, btnAdd),
                new HBox(10, btnEditar, btnRemover));

        // Localize estas linhas e aplique as alterações:
        HBox mainBox = new HBox(15, layoutEsquerdo, graficoFinanceiro); //REDUZI O ESPAÇAMENTO POR CAUSA DO ESPAÇO SOBRANDO NA PÁGINA
        mainBox.setAlignment(Pos.CENTER_LEFT); // Altera de CENTER para CENTER_LEFT
        HBox.setHgrow(layoutEsquerdo, Priority.ALWAYS);

        VBox root = new VBox(20, new Label("SISTEMA ESTOQUE ELITE PRO"), cardBox, mainBox);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT); // Garante que tudo comece alinhado à esquerda
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 1450, 880);
        aplicarCSS(scene);

        atualizarDashboard.run();
        stage.setScene(scene);
        stage.setTitle("ERP Elite Pro 2.0");
        stage.show();
    }

    private void configurarColunas(TableView<Produto> tabela) {
        TableColumn<Produto, String> c0 = new TableColumn<>("Cód");
        c0.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCodigo()));

        TableColumn<Produto, String> c1 = new TableColumn<>("Produto");
        c1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNome()));

        TableColumn<Produto, Number> c2 = new TableColumn<>("Qtd");
        c2.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getQuantidade()));

        TableColumn<Produto, Number> c3 = new TableColumn<>("P. Custo");
        c3.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getPreco()));

        TableColumn<Produto, Number> c4 = new TableColumn<>("P. Venda");
        c4.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getPrecoVenda()));

        // Ajuste de largura proporcional para 5 colunas
        c0.prefWidthProperty().bind(tabela.widthProperty().multiply(0.12));
        c1.prefWidthProperty().bind(tabela.widthProperty().multiply(0.39));
        c2.prefWidthProperty().bind(tabela.widthProperty().multiply(0.10));
        c3.prefWidthProperty().bind(tabela.widthProperty().multiply(0.19));
        c4.prefWidthProperty().bind(tabela.widthProperty().multiply(0.19));

        tabela.getColumns().setAll(c0, c1, c2, c3, c4);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void abrirJanelaEdicao(Produto p, Runnable callback) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Editar Produto");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("root");

        TextField tfCod = new TextField(p.getCodigo());
        TextField tfN = new TextField(p.getNome());
        TextField tfQ = new TextField(String.valueOf(p.getQuantidade()));
        TextField tfC = new TextField(String.valueOf(p.getPreco()));
        TextField tfV = new TextField(String.valueOf(p.getPrecoVenda()));
        ComboBox<String> cbC = new ComboBox<>(categorias); cbC.setValue(p.getCategoria());

        Button btnSalvar = new Button("Salvar Alterações");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        btnSalvar.setOnAction(e -> {
            try {
                p.setCodigo(tfCod.getText());
                p.setNome(tfN.getText());
                p.setQuantidade(Integer.parseInt(tfQ.getText()));
                p.setPreco(Double.parseDouble(tfC.getText()));
                p.setPrecoVenda(Double.parseDouble(tfV.getText()));
                p.setCategoria(cbC.getValue());
                service.atualizar(p, p);
                callback.run(); stage.close();
            } catch (Exception ex) { }
        });

        layout.getChildren().addAll(new Label("Código:"), tfCod, new Label("Nome:"), tfN,
                new Label("Qtd:"), tfQ, new Label("Custo:"), tfC,
                new Label("Venda:"), tfV, new Label("Categoria:"), cbC, btnSalvar);

        Scene scene = new Scene(layout, 380, 650);
        aplicarCSS(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void configurarTooltip(Control c, String msg) {
        Tooltip t = new Tooltip(msg);
        t.setShowDelay(Duration.millis(200));
        c.setTooltip(t);
    }

    private void aplicarCSS(Scene s) {
        URL url = getClass().getResource("/ui/style.css");
        if (url != null) s.getStylesheets().add(url.toExternalForm());
    }

    private VBox criarCard(String t, Label v, String cssClass) {
        Label lblT = new Label(t);
        lblT.getStyleClass().add("label-titulo-card");
        v.getStyleClass().add("label-valor");
        VBox card = new VBox(5, lblT, v);
        card.getStyleClass().addAll("card", cssClass);
        card.setAlignment(Pos.CENTER_LEFT);
        return card;
    }

    public static void main(String[] args) { launch(args); }
}