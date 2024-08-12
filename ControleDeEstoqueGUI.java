import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Produto implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int contador = 0;
    private int codigo;
    private String nome;
    private int quantidade;

    public Produto(String nome, int quantidade) {
        this.codigo = ++contador;
        this.nome = nome;
        this.quantidade = quantidade;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void adicionarQuantidade(int quantidade) {
        this.quantidade += quantidade;
    }

    public void removerQuantidade(int quantidade) {
        if (this.quantidade >= quantidade) {
            this.quantidade -= quantidade;
        } else {
            System.out.println("Quantidade insuficiente em estoque.");
        }
    }

    public static void setContador(int contador) {
        Produto.contador = contador;
    }

    @Override
    public String toString() {
        return "Código: " + codigo + ", Produto: " + nome + ", Quantidade: " + quantidade;
    }
}

public class ControleDeEstoqueGUI extends JFrame {
    private static ArrayList<Produto> produtos = new ArrayList<>();
    private static final String FILE_NAME = "produtos.dat";

    private JTextField nomeField, quantidadeField, consultaField;
    private JTextArea resultadoArea;

    public ControleDeEstoqueGUI() {
        super("Controle de Estoque");

        carregarDados();
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0));

        // Set dark theme colors
        Color backgroundColor = new Color(0, 0, 0);
        Color textColor = new Color(255, 255, 255);
        Color buttonColor = new Color(60, 63, 65);
        Color fieldColor = new Color(40, 40, 40);

        // Painel superior para consulta
        JPanel painelConsulta = new JPanel(new BorderLayout());
        painelConsulta.setBackground(backgroundColor);
        painelConsulta.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel consultaLabel = new JLabel("Consulta por Nome ou Código:");
        consultaLabel.setForeground(textColor);
        painelConsulta.add(consultaLabel, BorderLayout.NORTH);

        consultaField = new JTextField();
        consultaField.setBackground(fieldColor);
        consultaField.setForeground(textColor);
        painelConsulta.add(consultaField, BorderLayout.CENTER);

        add(painelConsulta, BorderLayout.NORTH);

        // Painel central para resultados e entrada de dados
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(backgroundColor);

        // Painel para resultados
        resultadoArea = new JTextArea(10, 30);
        resultadoArea.setEditable(false);
        resultadoArea.setBackground(fieldColor);
        resultadoArea.setForeground(textColor);
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        painelCentral.add(scrollPane, BorderLayout.CENTER);

        // Painel para entrada de dados
        JPanel painelEntrada = new JPanel(new GridLayout(4, 2));
        painelEntrada.setBackground(backgroundColor);

        JLabel nomeLabel = new JLabel("Nome do produto:");
        nomeLabel.setForeground(textColor);
        painelEntrada.add(nomeLabel);

        nomeField = new JTextField();
        nomeField.setBackground(fieldColor);
        nomeField.setForeground(textColor);
        painelEntrada.add(nomeField);

        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setForeground(textColor);
        painelEntrada.add(quantidadeLabel);

        quantidadeField = new JTextField();
        quantidadeField.setBackground(fieldColor);
        quantidadeField.setForeground(textColor);
        painelEntrada.add(quantidadeField);

        JButton cadastrarButton = new JButton("Cadastrar Produto");
        cadastrarButton.setBackground(buttonColor);
        cadastrarButton.setForeground(textColor);
        cadastrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarProduto();
            }
        });
        painelEntrada.add(cadastrarButton);

        painelCentral.add(painelEntrada, BorderLayout.SOUTH);

        add(painelCentral, BorderLayout.CENTER);

        // Painel direito para botões
        JPanel painelBotoes = new JPanel(new GridLayout(5, 1));
        painelBotoes.setBackground(backgroundColor);

        JButton entradaButton = new JButton("Entrada de Produtos");
        entradaButton.setBackground(buttonColor);
        entradaButton.setForeground(textColor);
        entradaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirPedidoEntrada();
            }
        });
        painelBotoes.add(entradaButton);

        JButton saidaButton = new JButton("Saída de Produtos");
        saidaButton.setBackground(buttonColor);
        saidaButton.setForeground(textColor);
        saidaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirPedidoSaida();
            }
        });
        painelBotoes.add(saidaButton);

        JButton listarButton = new JButton("Listar Produtos");
        listarButton.setBackground(buttonColor);
        listarButton.setForeground(textColor);
        listarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listarProdutos();
            }
        });
        painelBotoes.add(listarButton);

        JButton consultarButton = new JButton("Consultar Produto");
        consultarButton.setBackground(buttonColor);
        consultarButton.setForeground(textColor);
        consultarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                consultarProduto();
            }
        });
        painelBotoes.add(consultarButton);

        JButton removerButton = new JButton("Remover Produto");
        removerButton.setBackground(buttonColor);
        removerButton.setForeground(textColor);
        removerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removerProduto();
            }
        });
        painelBotoes.add(removerButton);

        add(painelBotoes, BorderLayout.EAST);

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void cadastrarProduto() {
        String nome = nomeField.getText();
        int quantidade = Integer.parseInt(quantidadeField.getText());

        produtos.add(new Produto(nome, quantidade));
        salvarDados();
        resultadoArea.setText("Produto cadastrado com sucesso!\n");
        nomeField.setText("");
        quantidadeField.setText("");
    }

    private void abrirPedidoEntrada() {
        // Criar janela de pedido de entrada
        JDialog dialog = new JDialog(this, "Pedido de Entrada", true);
        dialog.setLayout(new BorderLayout());

        JTextArea pedidoArea = new JTextArea(10, 30);
        pedidoArea.setEditable(false);
        pedidoArea.setBackground(new Color(40, 40, 40));
        pedidoArea.setForeground(new Color(255, 255, 255));
        JScrollPane scrollPane = new JScrollPane(pedidoArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBusca = new JPanel(new BorderLayout());
        JTextField buscaField = new JTextField();
        buscaField.setBackground(new Color(40, 40, 40));
        buscaField.setForeground(new Color(255, 255, 255));
        painelBusca.add(new JLabel("Buscar Produto:"), BorderLayout.WEST);
        painelBusca.add(buscaField, BorderLayout.CENTER);

        JButton buscarButton = new JButton("Buscar");
        buscarButton.setBackground(new Color(60, 63, 65));
        buscarButton.setForeground(new Color(255, 255, 255));
        painelBusca.add(buscarButton, BorderLayout.EAST);

        dialog.add(painelBusca, BorderLayout.NORTH);

        JButton concluirButton = new JButton("Concluir");
        concluirButton.setBackground(new Color(60, 63, 65));
        concluirButton.setForeground(new Color(255, 255, 255));

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(concluirButton);
        dialog.add(painelBotoes, BorderLayout.SOUTH);

        buscarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String busca = buscaField.getText();
                List<Produto> resultados = buscarProdutos(busca);
                pedidoArea.setText("");
                if (resultados.isEmpty()) {
                    pedidoArea.append("Nenhum produto encontrado.\n");
                } else {
                    for (Produto produto : resultados) {
                        String quantidadeStr = JOptionPane.showInputDialog("Quantidade a adicionar para o produto " + produto.getNome() + " (Código: " + produto.getCodigo() + "):");
                        try {
                            int quantidade = Integer.parseInt(quantidadeStr);
                            produto.adicionarQuantidade(quantidade);
                            pedidoArea.append(produto.getNome() + ": " + quantidade + " unidades adicionadas.\n");
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        concluirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirmacao = JOptionPane.showConfirmDialog(dialog, "Você tem certeza que deseja concluir a transação?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirmacao == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(dialog, "Transação concluída com sucesso!\n" + pedidoArea.getText(), "Confirmação", JOptionPane.INFORMATION_MESSAGE);
                    salvarDados();
                    dialog.dispose();
                }
            }
        });

        dialog.setSize(500, 400);
        dialog.setVisible(true);
    }

    private void abrirPedidoSaida() {
        // Criar janela de pedido de saída
        JDialog dialog = new JDialog(this, "Pedido de Saída", true);
        dialog.setLayout(new BorderLayout());

        JTextArea pedidoArea = new JTextArea(10, 30);
        pedidoArea.setEditable(false);
        pedidoArea.setBackground(new Color(40, 40, 40));
        pedidoArea.setForeground(new Color(255, 255, 255));
        JScrollPane scrollPane = new JScrollPane(pedidoArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBusca = new JPanel(new BorderLayout());
        JTextField buscaField = new JTextField();
        buscaField.setBackground(new Color(40, 40, 40));
        buscaField.setForeground(new Color(255, 255, 255));
        painelBusca.add(new JLabel("Buscar Produto:"), BorderLayout.WEST);
        painelBusca.add(buscaField, BorderLayout.CENTER);

        JButton buscarButton = new JButton("Buscar");
        buscarButton.setBackground(new Color(60, 63, 65));
        buscarButton.setForeground(new Color(255, 255, 255));
        painelBusca.add(buscarButton, BorderLayout.EAST);

        dialog.add(painelBusca, BorderLayout.NORTH);

        JButton concluirButton = new JButton("Concluir");
        concluirButton.setBackground(new Color(60, 63, 65));
        concluirButton.setForeground(new Color(255, 255, 255));

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(concluirButton);
        dialog.add(painelBotoes, BorderLayout.SOUTH);

        buscarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String busca = buscaField.getText();
                List<Produto> resultados = buscarProdutos(busca);
                pedidoArea.setText("");
                if (resultados.isEmpty()) {
                    pedidoArea.append("Nenhum produto encontrado.\n");
                } else {
                    for (Produto produto : resultados) {
                        String quantidadeStr = JOptionPane.showInputDialog("Quantidade a retirar para o produto " + produto.getNome() + " (Código: " + produto.getCodigo() + "):");
                        try {
                            int quantidade = Integer.parseInt(quantidadeStr);
                            produto.removerQuantidade(quantidade);
                            pedidoArea.append(produto.getNome() + ": " + quantidade + " unidades removidas.\n");
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        concluirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirmacao = JOptionPane.showConfirmDialog(dialog, "Você tem certeza que deseja concluir a transação?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirmacao == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(dialog, "Transação concluída com sucesso!\n" + pedidoArea.getText(), "Confirmação", JOptionPane.INFORMATION_MESSAGE);
                    salvarDados();
                    dialog.dispose();
                }
            }
        });

        dialog.setSize(500, 400);
        dialog.setVisible(true);
    }

    private List<Produto> buscarProdutos(String busca) {
        return produtos.stream()
            .filter(p -> busca.equalsIgnoreCase(String.valueOf(p.getCodigo())) || // Pesquisa por código
                    p.getNome().toLowerCase().contains(busca.toLowerCase().replace("%", ""))) // Pesquisa por nome completo ou parcial
            .collect(Collectors.toList());
    }

    private void listarProdutos() {
        resultadoArea.setText("");
        if (produtos.isEmpty()) {
            resultadoArea.append("Nenhum produto cadastrado.\n");
        } else {
            for (Produto produto : produtos) {
                resultadoArea.append(produto.toString() + "\n");
            }
        }
    }

    private void consultarProduto() {
        String consulta = consultaField.getText();
        List<Produto> resultados = buscarProdutos(consulta);
        resultadoArea.setText("");
        if (resultados.isEmpty()) {
            resultadoArea.append("Nenhum produto encontrado.\n");
        } else {
            for (Produto produto : resultados) {
                resultadoArea.append(produto.toString() + "\n");
            }
        }
    }

    private void removerProduto() {
        String codigoStr = JOptionPane.showInputDialog("Digite o código do produto a ser removido:");
        try {
            int codigo = Integer.parseInt(codigoStr);
            produtos.removeIf(p -> p.getCodigo() == codigo);
            salvarDados();
            JOptionPane.showMessageDialog(this, "Produto removido com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Código inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarDados() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(produtos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarDados() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                produtos = (ArrayList<Produto>) ois.readObject();
                if (!produtos.isEmpty()) {
                    Produto.setContador(produtos.get(produtos.size() - 1).getCodigo());
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControleDeEstoqueGUI());
    }
}
