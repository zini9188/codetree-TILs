import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static int N, M, K, C;
    static Set<Tree> trees;
    static int[][] treeMap;
    static int[][] obstacles;
    static int[] dx = {1, -1, 0, 0, 1, 1, -1, -1}, dy = {0, 0, 1, -1, -1, 1, -1, 1};
    static int ans = 0;

    public static void main(String[] args) throws IOException {
        init();
        simulate();
    }

    private static void simulate() {
        // M년동안
        for (int year = 1; year <= M; year++) {
            growTrees();
            spreadTrees(year);
            putObstacle(year);
//            print();
        }
        System.out.println(ans);
    }

    private static void print() {
        System.out.println(Arrays.deepToString(obstacles).replaceAll("],", "\n"));
        System.out.println(Arrays.deepToString(treeMap).replaceAll("],", "\n"));
    }

    private static void putObstacle(int year) {
        int maxCount = -1;
        int px = -1, py = -1;

        for (int i = N - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (treeMap[i][j] > 0) {
                    int count = treeMap[i][j];
                    for (int k = 4; k < 8; k++) {

                        // l번 반복
                        for (int l = 1; l <= K; l++) {
                            int nx = i + dx[k] * l;
                            int ny = j + dy[k] * l;
                            if (outRange(nx, ny) || treeMap[nx][ny] <= 0) {
                                break;
                            }
                            count += treeMap[nx][ny];
                        }
                    }

                    if (count >= maxCount) {
                        maxCount = count;
                        px = i;
                        py = j;
                    }
                }
            }
        }

        if (maxCount == -1 || px == -1 || py == -1) {
            return;
        }

        trees.remove(new Tree(px, py));
        ans += treeMap[px][py];
        treeMap[px][py] = 0;
        obstacles[px][py] = year + C;
        for (int k = 4; k < 8; k++) {
            for (int l = 1; l <= K; l++) {
                int nx = px + dx[k] * l;
                int ny = py + dy[k] * l;
                if (outRange(nx, ny)) {
                    break;
                }

                if (treeMap[nx][ny] <= 0) {
                    obstacles[nx][ny] = year + C;
                    break;
                }

                obstacles[nx][ny] = year + C;
                trees.remove(new Tree(nx, ny));
                ans += treeMap[nx][ny];
                treeMap[nx][ny] = 0;
            }
        }


    }

    private static void spreadTrees(int year) {
        Queue<Tree> spreadTrees = new ArrayDeque<>();
        for (Tree tree : trees) {
            int spread = tree.spreadCount(year);
            if (spread == 0) {
                continue;
            }

            for (int i = 0; i < 4; i++) {
                int nx = tree.x + dx[i];
                int ny = tree.y + dy[i];
                if (outRange(nx, ny)) {
                    continue;
                }

                if (obstacles[nx][ny] < year && treeMap[nx][ny] == 0) {
                    spreadTrees.add(new Tree(nx, ny, spread));
                }
            }
        }

        Set<Tree> setTrees = new HashSet<>();
        while (!spreadTrees.isEmpty()) {
            Tree tree = spreadTrees.poll();
            treeMap[tree.x][tree.y] += tree.c;
            setTrees.add(new Tree(tree.x, tree.y));
        }

        trees.addAll(setTrees);
    }

    private static void growTrees() {
        for (Tree tree : trees) {
            tree.grow();
        }
    }

    private static void init() throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());

        trees = new HashSet<>();
        treeMap = new int[N][N];
        obstacles = new int[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                treeMap[i][j] = Integer.parseInt(st.nextToken());
                if (treeMap[i][j] > 0) {
                    trees.add(new Tree(i, j));
                }
            }
        }
    }

    static boolean outRange(int x, int y) {
        return x < 0 || y < 0 || x >= N || y >= N;
    }

    static class Tree {

        int x, y, c;

        public Tree(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Tree(int x, int y, int c) {
            this.x = x;
            this.y = y;
            this.c = c;
        }

        public void grow() {
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if (outRange(nx, ny)) {
                    continue;
                }

                if (treeMap[nx][ny] > 0) {
                    treeMap[x][y]++;
                }
            }
        }

        public int spreadCount(int year) {
            int count = 0;
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if (outRange(nx, ny)) {
                    continue;
                }

                if (treeMap[nx][ny] == 0 && obstacles[nx][ny] < year) {
                    count++;
                }
            }
            if (count == 0) {
                return 0;
            }

            return treeMap[x][y] / count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Tree tree = (Tree) o;
            return x == tree.x && y == tree.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}