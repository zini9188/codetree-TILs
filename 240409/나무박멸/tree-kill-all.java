import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    // 격자 크기 n, 박멸년수, 제초제 퍼지는 범위 k, 제초제 남는 수 c
    static int N, M, K, C;
    static int[][] map;
    static int[][] treeMap;
    // 살아있는 나무들
    static Set<Tree> trees;
    static int ans;
    static int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1}, dy = {0, 0, 1, -1, -1, 1, -1, 1};


    public static void main(String[] args) throws IOException {
        init();
        simulate();
    }

    private static void simulate() {
        for (int year = 1; year <= M; year++) {
            growTrees();
            spreadTrees(year);
            destroy(year);
//            print();
        }
        System.out.println(ans);
    }

    static void print() {
        System.out.println(Arrays.deepToString(map).replaceAll("],", "\n").replaceAll("-1", "ㅁ"));
        System.out.println(Arrays.deepToString(treeMap).replaceAll("],", "\n"));
        System.out.println(trees);
    }

    private static void init() throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());

        trees = new LinkedHashSet<>();
        map = new int[N][N];
        treeMap = new int[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                treeMap[i][j] = Integer.parseInt(st.nextToken());
                if (treeMap[i][j] > 0) {
                    trees.add(new Tree(i, j));
                }
            }
        }
        ans = 0;
    }

    // 인접한 네 개의 칸 중 나무가 있는 칸의 수만큼 나무 성장
    // 성장은 모든 나무에게 동시에 일어남
    public static void growTrees() {
        Queue<Tree> queue = new ArrayDeque<>();
        for (Tree tree : trees) {
            int count = 0;
            for (int j = 0; j < 4; j++) {
                int nx = tree.x + dx[j];
                int ny = tree.y + dy[j];

                if (outRange(nx, ny) || treeMap[nx][ny] <= 0) {
                    continue;
                }
                count++;
            }
            queue.add(new Tree(tree.x, tree.y, count));
        }

        while (!queue.isEmpty()) {
            Tree tree = queue.poll();
            treeMap[tree.x][tree.y] += tree.c;
        }
    }

    private static boolean outRange(int x, int y) {
        return x < 0 || y < 0 || x >= N || y >= N;
    }

    // 기존에 있었던 나무들은 인접한 4개의 칸 중 벽, 다른 나무, 제초제
    // 모두 없는 칸에 번식
    // 각 칸의 나무 그루 수에서 번식이 가능한 칸의 개수만큼 나누어진 그루 수만큼 번식
    // 나머지 버림
    // 모든 나무가 동시에
    public static void spreadTrees(int year) {
        Queue<Tree> newTrees = new ArrayDeque<>();
        for (Tree tree : trees) {
            int count = 0;
            for (int j = 0; j < 4; j++) {
                int nx = tree.x + dx[j];
                int ny = tree.y + dy[j];

                // 격자 밖, 제초제가 있는 경우, 빈땅만 가능
                if (outRange(nx, ny) || map[nx][ny] >= year || treeMap[nx][ny] != 0) {
                    continue;
                }

                count++;
            }

            // 나무를 놓을 곳이 없으면
            if (count == 0) {
                continue;
            }

            for (int j = 0; j < 4; j++) {
                int nx = tree.x + dx[j];
                int ny = tree.y + dy[j];

                if (outRange(nx, ny) || map[nx][ny] >= year || treeMap[nx][ny] != 0) {
                    continue;
                }

                newTrees.add(new Tree(nx, ny, treeMap[tree.x][tree.y] / count));
            }
        }

        Set<Tree> setTrees = new HashSet<>();
        while (!newTrees.isEmpty()) {
            Tree tree = newTrees.poll();
            setTrees.add(tree);
            treeMap[tree.x][tree.y] += tree.c;
        }
        trees.addAll(setTrees);
    }

    // 각 칸 중 제초제를 뿌렸을 때, 가장 많이 나무 박멸되는 칸에 뿌림
    // 나무 없는 칸에 제초제 뿌리면 박멸되는 나무 전혀 없음
    // 나무 있으면 4개의 대각선 방향으로 k칸 만큼 전파
    // 전파되는 도중 벽이 있거나 나무가 없다면 그 칸까지만 뿌림
    // c년만큼 제초제가 남아있다가 c+1년째가 될때 사라짐
    // 다시 제초제 뿌려지면 새로 뿌려진 해로부터 c년동안 유지
    public static void destroy(int year) {
        // 최대 개수, x, y
        int[] info = {-1, -1, -1};

        for (int x = N - 1; x >= 0; x--) {
            for (int y = N - 1; y >= 0; y--) {
                if (treeMap[x][y] > 0) {
                    // 개수
                    int count = treeMap[x][y];
                    for (int k = 4; k < 8; k++) {
                        // k만큼 간다.
                        for (int l = 1; l <= K; l++) {
                            int nx = x + dx[k] * l;
                            int ny = y + dy[k] * l;

                            // 범위 밖, 벽이거나 나무가 없으면 넘어감
                            if (outRange(nx, ny) || treeMap[nx][ny] <= 0) {
                                break;
                            }

                            count += treeMap[nx][ny];
                        }
                    }

                    if (count >= info[0]) {
                        info[1] = x;
                        info[2] = y;
                        info[0] = count;
                    }
                }
            }
        }

        if (info[0] == -1 || info[1] == -1 || info[2] == -1) {
            return;
        }

        // 해당 나무 없앰
        ans += treeMap[info[1]][info[2]];
        trees.remove(new Tree(info[1], info[2]));
        treeMap[info[1]][info[2]] = 0;
        // 제초제 뿌림
        map[info[1]][info[2]] = year + C;
        // 대각선으로 퍼짐
        for (int k = 4; k < 8; k++) {
            // k만큼 간다.
            for (int l = 1; l <= K; l++) {
                int nx = info[1] + dx[k] * l;
                int ny = info[2] + dy[k] * l;

                // 범위 밖, 벽이거나 나무가 없으면 넘어감
                if (outRange(nx, ny) || treeMap[nx][ny] <= 0) {
                    break;
                }

                ans += treeMap[nx][ny];
                trees.remove(new Tree(nx, ny));
                treeMap[nx][ny] = 0;
                map[nx][ny] = year + C;
            }
        }
    }

    static class Tree {

        int x, y, c;

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

        public Tree(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Tree(int x, int y, int c) {
            this.x = x;
            this.y = y;
            this.c = c;
        }

        @Override
        public String toString() {
            return "Tree{" + "x=" + x + ", y=" + y + ", c=" + c + '}';
        }
    }
}