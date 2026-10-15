/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.editorgrafos;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


public class EditorGrafos extends JFrame {
    private JTextArea inputMatrix;
    private JTextArea outputLog;
    private GraphPanel graphPanel;
    private JRadioButton radioDirected, radioUndirected;
    private JTextField txtSource, txtTarget; // Para Dijkstra y Floyd
    private int[][] matrix;
    private final int NUM_NODES = 15;

    public EditorGrafos() {
        setTitle("Editor de Grafos BUAP - Otogno 2026");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel izquierdo: Controles y Matrices
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(450, 700));

        // Sub-panel superior: Input de la matriz
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Matriz de Adyacencia Inicial (15x15)"));
        inputMatrix = new JTextArea();
        inputMatrix.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputPanel.add(new JScrollPane(inputMatrix), BorderLayout.CENTER);

        JButton btnLoadDefault = new JButton("Cargar Datos de Prueba (15 Nodos)");
        btnLoadDefault.addActionListener(e -> loadDefaultMatrix());
        inputPanel.add(btnLoadDefault, BorderLayout.SOUTH);

        // Sub-panel central: Opciones y Algoritmos
        JPanel controlsPanel = new JPanel(new GridLayout(9, 1, 2, 2));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Configuración y Algoritmos"));

        radioDirected = new JRadioButton("Grafo Dirigido");
        radioUndirected = new JRadioButton("Grafo No Dirigido", true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(radioDirected);
        bg.add(radioUndirected);

        JPanel radioPanel = new JPanel();
        radioPanel.add(radioDirected);
        radioPanel.add(radioUndirected);
        controlsPanel.add(radioPanel);

        // Inputs para Nodos Origen/Destino
        JPanel nodeInputPanel = new JPanel(new FlowLayout());
        nodeInputPanel.add(new JLabel("Origen (1-15):"));
        txtSource = new JTextField("1", 3);
        nodeInputPanel.add(txtSource);
        nodeInputPanel.add(new JLabel(" Destino (1-15):"));
        txtTarget = new JTextField("15", 3);
        nodeInputPanel.add(txtTarget);
        controlsPanel.add(nodeInputPanel);

        JButton btnDraw = new JButton("Visualizar Grafo");
        JButton btnKruskal = new JButton("Algoritmo de Kruskal (MST)");
        JButton btnPrim = new JButton("Algoritmo de Prim (MST)");
        JButton btnDijkstra = new JButton("Algoritmo de Dijkstra (Camino Mínimo)");
        JButton btnFloyd = new JButton("Algoritmo de Floyd (Pares de Nodos)");

        controlsPanel.add(btnDraw);
        controlsPanel.add(btnKruskal);
        controlsPanel.add(btnPrim);
        controlsPanel.add(btnDijkstra);
        controlsPanel.add(btnFloyd);

        // Sub-panel inferior: Consola de salida
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Resultados y Matrices"));
        outputLog = new JTextArea();
        outputLog.setEditable(false);
        outputLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        outputLog.setBackground(new Color(245, 245, 245));
        logPanel.add(new JScrollPane(outputLog), BorderLayout.CENTER);

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.add(inputPanel, BorderLayout.NORTH);
        leftContainer.add(controlsPanel, BorderLayout.CENTER);
        leftContainer.add(logPanel, BorderLayout.SOUTH);
        inputPanel.setPreferredSize(new Dimension(450, 250));
        logPanel.setPreferredSize(new Dimension(450, 300));

        leftPanel.add(leftContainer, BorderLayout.CENTER);

        graphPanel = new GraphPanel();
        add(leftPanel, BorderLayout.WEST);
        add(graphPanel, BorderLayout.CENTER);

        // Listeners
        btnDraw.addActionListener(e -> parseAndDraw(null));
        btnKruskal.addActionListener(e -> runKruskal());
        btnPrim.addActionListener(e -> runPrim());
        btnDijkstra.addActionListener(e -> runDijkstra());
        btnFloyd.addActionListener(e -> runFloyd());

        loadDefaultMatrix();
    }

    private void loadDefaultMatrix() {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random(50);
        for (int i = 0; i < NUM_NODES; i++) {
            for (int j = 0; j < NUM_NODES; j++) {
                if (i == j) sb.append("0 ");
                else {
                    int weight = rand.nextDouble() > 0.88 ? rand.nextInt(15) + 1 : 0;
                    sb.append(weight).append(" ");
                }
            }
            sb.append("\n");
        }
        inputMatrix.setText(sb.toString());
    }

    private void printInitialMatrix() {
        outputLog.append("--- MATRIZ DE ADYACENCIA INICIAL ---\n");
        for (int i = 0; i < NUM_NODES; i++) {
            for (int j = 0; j < NUM_NODES; j++) {
                outputLog.append(matrix[i][j] + "\t");
            }
            outputLog.append("\n");
        }
        outputLog.append("\n");
    }

    private boolean parseMatrix() {
        try {
            matrix = new int[NUM_NODES][NUM_NODES];
            String[] lines = inputMatrix.getText().trim().split("\n");
            for (int i = 0; i < Math.min(NUM_NODES, lines.length); i++) {
                String[] tokens = lines[i].trim().split("\\s+");
                for (int j = 0; j < Math.min(NUM_NODES, tokens.length); j++) {
                    matrix[i][j] = Integer.parseInt(tokens[j]);
                }
            }
            if (radioUndirected.isSelected()) {
                for (int i = 0; i < NUM_NODES; i++) {
                    for (int j = i + 1; j < NUM_NODES; j++) {
                        if (matrix[i][j] > 0) matrix[j][i] = matrix[i][j];
                        else if (matrix[j][i] > 0) matrix[i][j] = matrix[j][i];
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            outputLog.setText("ERROR: Formato de matriz inválido. Use números separados por espacios.");
            return false;
        }
    }

    private void parseAndDraw(List<Edge> highlightEdges) {
        if (parseMatrix()) {
            graphPanel.setGraph(matrix, radioDirected.isSelected(), highlightEdges);
            outputLog.setText("Grafo visualizado correctamente.\n");
        }
    }

    // --- ALGORITMOS ---

    private void runKruskal() {
        if (!parseMatrix()) return;
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < NUM_NODES; i++) {
            for (int j = radioUndirected.isSelected() ? i + 1 : 0; j < NUM_NODES; j++) {
                if (matrix[i][j] > 0) edges.add(new Edge(i, j, matrix[i][j]));
            }
        }
        
        outputLog.setText("--- ALGORITMO DE KRUSKAL ---\n");
        outputLog.append("1. Ordenamiento de aristas por peso:\n");
        Collections.sort(edges);
        for (Edge e : edges) outputLog.append("Arista ("+(e.u+1)+"-"+(e.v+1)+") Peso: "+e.w+"\n");

        int[] parent = new int[NUM_NODES];
        for (int i = 0; i < NUM_NODES; i++) parent[i] = i;

        List<Edge> mst = new ArrayList<>();
        int totalCost = 0;
        outputLog.append("\n2. Construcción del Árbol (Evitando Ciclos):\n");

        for (Edge e : edges) {
            int rootU = find(parent, e.u);
            int rootV = find(parent, e.v);
            if (rootU != rootV) {
                mst.add(e);
                totalCost += e.w;
                parent[rootU] = rootV;
                outputLog.append("Añadida: ("+(e.u+1)+"-"+(e.v+1)+") Peso: "+e.w+"\n");
            }
        }
        outputLog.append("\nPESO TOTAL MST: " + totalCost);
        graphPanel.setGraph(matrix, radioDirected.isSelected(), mst);
    }

    private int find(int[] parent, int i) {
        if (parent[i] == i) return i;
        return parent[i] = find(parent, parent[i]);
    }

    private void runPrim() {
        if (!parseMatrix()) return;
        boolean[] inTree = new boolean[NUM_NODES];
        int[] minWeight = new int[NUM_NODES];
        int[] parent = new int[NUM_NODES];
        Arrays.fill(minWeight, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        minWeight[0] = 0; // Inicia en nodo 1 (índice 0)
        List<Edge> mst = new ArrayList<>();
        int totalCost = 0;
        outputLog.setText("--- ALGORITMO DE PRIM (Desde Nodo 1) ---\n");

        for (int i = 0; i < NUM_NODES; i++) {
            int u = -1;
            for (int j = 0; j < NUM_NODES; j++) {
                if (!inTree[j] && (u == -1 || minWeight[j] < minWeight[u])) u = j;
            }
            if (u == -1 || minWeight[u] == Integer.MAX_VALUE) break;
            inTree[u] = true;

            if (parent[u] != -1) {
                mst.add(new Edge(parent[u], u, matrix[parent[u]][u]));
                totalCost += matrix[parent[u]][u];
                outputLog.append("Arista: ("+(parent[u]+1)+","+(u+1)+") Costo: "+matrix[parent[u]][u]+"\n");
            }

            for (int v = 0; v < NUM_NODES; v++) {
                if (matrix[u][v] > 0 && !inTree[v] && matrix[u][v] < minWeight[v]) {
                    minWeight[v] = matrix[u][v];
                    parent[v] = u;
                }
            }
        }
        outputLog.append("\nCOSTO TOTAL: " + totalCost);
        graphPanel.setGraph(matrix, radioDirected.isSelected(), mst);
    }

    private void runDijkstra() {
        if (!parseMatrix()) return;
        int target;
        try { target = Integer.parseInt(txtTarget.getText()) - 1; } catch (Exception e) { target = 14; }
        
        int startNode = 0;
        int[] dist = new int[NUM_NODES];
        int[] parent = new int[NUM_NODES];
        boolean[] visited = new boolean[NUM_NODES];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[startNode] = 0;

        for (int i = 0; i < NUM_NODES; i++) {
            int u = -1;
            for (int j = 0; j < NUM_NODES; j++) {
                if (!visited[j] && (u == -1 || dist[j] < dist[u])) u = j;
            }
            if (u == -1 || dist[u] == Integer.MAX_VALUE) break;
            visited[u] = true;
            for (int v = 0; v < NUM_NODES; v++) {
                if (matrix[u][v] > 0 && dist[u] + matrix[u][v] < dist[v]) {
                    dist[v] = dist[u] + matrix[u][v];
                    parent[v] = u;
                }
            }
        }

        outputLog.setText("");
        printInitialMatrix();
        outputLog.append("--- RESULTADOS DIJKSTRA (Nodo 1 a todos) ---\nDestino\tDistancia\n");
        for (int i = 0; i < NUM_NODES; i++) {
            outputLog.append("Nodo " + (i+1) + "\t" + (dist[i] == Integer.MAX_VALUE ? "INF" : dist[i]) + "\n");
        }

        List<Edge> path = new ArrayList<>();
        int curr = target;
        while (curr != -1 && parent[curr] != -1) {
            path.add(new Edge(parent[curr], curr, matrix[parent[curr]][curr]));
            curr = parent[curr];
        }
        graphPanel.setGraph(matrix, radioDirected.isSelected(), path);
    }

    private void runFloyd() {
        if (!parseMatrix()) return;
        int src, dest;
        try {
            src = Integer.parseInt(txtSource.getText()) - 1;
            dest = Integer.parseInt(txtTarget.getText()) - 1;
        } catch (Exception e) { src = 0; dest = 14; }

        int[][] dist = new int[NUM_NODES][NUM_NODES];
        int[][] next = new int[NUM_NODES][NUM_NODES];

        for (int i = 0; i < NUM_NODES; i++) {
            for (int j = 0; j < NUM_NODES; j++) {
                if (i == j) dist[i][j] = 0;
                else if (matrix[i][j] > 0) {
                    dist[i][j] = matrix[i][j];
                    next[i][j] = j;
                } else {
                    dist[i][j] = 9999;
                    next[i][j] = -1;
                }
            }
        }

        for (int k = 0; k < NUM_NODES; k++) {
            for (int i = 0; i < NUM_NODES; i++) {
                for (int j = 0; j < NUM_NODES; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        outputLog.setText("");
        printInitialMatrix();
        outputLog.append("--- MATRIZ FINAL FLOYD-WARSHALL ---\n");
        for (int i = 0; i < NUM_NODES; i++) {
            for (int j = 0; j < NUM_NODES; j++) outputLog.append((dist[i][j] >= 9999 ? "∞" : dist[i][j]) + "\t");
            outputLog.append("\n");
        }

        List<Edge> path = new ArrayList<>();
        if (src >= 0 && dest < NUM_NODES && next[src][dest] != -1) {
            int u = src;
            while (u != dest) {
                int v = next[u][dest];
                path.add(new Edge(u, v, matrix[u][v]));
                u = v;
            }
        }
        graphPanel.setGraph(matrix, radioDirected.isSelected(), path);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditorGrafos().setVisible(true));
    }
}

class Edge implements Comparable<Edge> {
    int u, v, w;
    public Edge(int u, int v, int w) { this.u = u; this.v = v; this.w = w; }
    @Override
    public int compareTo(Edge o) { return Integer.compare(this.w, o.w); }
}

class GraphPanel extends JPanel {
    private int[][] matrix;
    private boolean directed;
    private List<Edge> highlightEdges;
    private Point[] nodePositions;

    public void setGraph(int[][] matrix, boolean directed, List<Edge> highlight) {
        this.matrix = matrix;
        this.directed = directed;
        this.highlightEdges = highlight;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (matrix == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int n = matrix.length;
        nodePositions = new Point[n];
        int centerX = getWidth() / 2, centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 80;

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            nodePositions[i] = new Point((int)(centerX + radius * Math.cos(angle)), (int)(centerY + radius * Math.sin(angle)));
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] > 0) drawEdge(g2, i, j, matrix[i][j]);
            }
        }

        for (int i = 0; i < n; i++) {
            g2.setColor(new Color(173, 216, 230));
            g2.fillOval(nodePositions[i].x - 15, nodePositions[i].y - 15, 30, 30);
            g2.setColor(Color.BLACK);
            g2.drawOval(nodePositions[i].x - 15, nodePositions[i].y - 15, 30, 30);
            g2.drawString(String.valueOf(i + 1), nodePositions[i].x - 5, nodePositions[i].y + 5);
        }
    }

    private void drawEdge(Graphics2D g2, int u, int v, int weight) {
        boolean highlight = false;
        if (highlightEdges != null) {
            for (Edge e : highlightEdges) {
                if ((e.u == u && e.v == v) || (!directed && e.u == v && e.v == u)) {
                    highlight = true; break;
                }
            }
        }

        int x1 = nodePositions[u].x, y1 = nodePositions[u].y;
        int x2 = nodePositions[v].x, y2 = nodePositions[v].y;

        g2.setColor(highlight ? Color.RED : Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(highlight ? 3 : 1));
        g2.drawLine(x1, y1, x2, y2);

        if (directed) {
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int arrowSize = 10;
            int offset = 16;
            int tx = x2 - (int)(offset * Math.cos(angle));
            int ty = y2 - (int)(offset * Math.sin(angle));
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(tx, ty);
            arrowHead.addPoint((int)(tx - arrowSize * Math.cos(angle - Math.PI/6)), (int)(ty - arrowSize * Math.sin(angle - Math.PI/6)));
            arrowHead.addPoint((int)(tx - arrowSize * Math.cos(angle + Math.PI/6)), (int)(ty - arrowSize * Math.sin(angle + Math.PI/6)));
            g2.fill(arrowHead);
        }

        g2.setColor(highlight ? Color.RED : new Color(0, 100, 0));
        g2.drawString(String.valueOf(weight), (x1 + x2) / 2, (y1 + y2) / 2);
    }
}
