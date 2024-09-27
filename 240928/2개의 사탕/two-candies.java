import java.io.*;
import java.util.*;

public class Main {

    private static int[] dx = {-1, 1, 0, 0}, dy = {0, 0, -1, 1};
    private static char[][] map;
    private static Element element;
    private static int N;
    private static int M;

    public static void main(String[] args) throws IOException {
        input();
        simulation();
    }

    private static void simulation() {
        int cnt = bfs();
        System.out.println(cnt);
    }

    private static int bfs() {
        Queue<Element> q = new ArrayDeque<>();
        q.add(element);
        boolean[][][][][] visited = new boolean[4][N][M][N][M];

        for (int i = 1; i < 10; i++) {
            int size = q.size();
            for (int j = 0; j < size; j++) {
                Element e = q.poll();
                for (int k = 0; k < 4; k++) {
                    int bCnt = 0, rCnt = 0;
                    int bNx = e.blue.x, bNy = e.blue.y, rNx = e.red.x, rNy = e.red.y;
                    while (true) {
                        if (outRange(bNx + dx[k], bNy + dy[k])) {
                            break;
                        }

                        bNx += dx[k];
                        bNy += dy[k];
                        bCnt++;

                        if (map[bNx][bNy] == 'O') {
                            break;
                        }
                    }

                    while (true) {
                        if (outRange(rNx + dx[k], rNy + dy[k])) {
                            break;
                        }

                        rNx += dx[k];
                        rNy += dy[k];
                        rCnt++;

                        if (map[rNx][rNy] == 'O') {
                            break;
                        }
                    }

                    if (rCnt == 0 && bCnt == 0) {
                        continue;
                    }

                    if (bNx == rNx && bNy == rNy) {
                        if (map[bNx][bNy] == 'O' && map[rNx][rNy] == 'O') {
                            continue;
                        }

                        if (bCnt > rCnt) {
                            bNx -= dx[k];
                            bNy -= dy[k];
                        } else if (rCnt > bCnt) {
                            rNx -= dx[k];
                            rNy -= dy[k];
                        }
                    }

                    if (visited[k][bNx][bNy][rNx][rNy]) {
                        continue;
                    }

                    if (map[bNx][bNy] == 'O') {
                        continue;
                    }

                    if (map[rNx][rNy] == 'O') {
                        return i;
                    }

                    q.add(new Element(new Candy(bNx, bNy), new Candy(rNx, rNy)));
                }
            }
        }

        return -1;
    }

    private static boolean outRange(int x, int y) {
        return x < 0 || y < 0 || x >= N || y >= M || map[x][y] == '#';
    }

    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        Candy blue = null, red = null;
        map = new char[N][M];
        for (int i = 0; i < N; i++) {
            map[i] = br.readLine().toCharArray();
            for (int j = 0; j < M; j++) {
                if (map[i][j] == 'B') {
                    blue = new Candy(i, j);
                    map[i][j] = '.';
                } else if (map[i][j] == 'R') {
                    red = new Candy(i, j);
                    map[i][j] = '.';
                }
            }
        }
        element = new Element(blue, red);
    }

    static class Element {

        Candy blue, red;

        public Element(Candy blue, Candy red) {
            this.blue = blue;
            this.red = red;
        }
    }

    static class Candy {

        int x, y;

        public Candy(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}