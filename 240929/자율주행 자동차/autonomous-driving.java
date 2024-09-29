import java.io.*;
import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        int d = Integer.parseInt(st.nextToken());

        Car car = new Car(x, y, d);

        int[][] road = new int[N][M];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                road[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

        boolean[][] visited = new boolean[N][M];
        visited[x][y] = true;
        int dist = 1;
        while (true) {
            boolean flag = false;
            int nd = car.d;
            for (int i = 0; i < 4; i++) {
                nd = (nd + 3) % 4;
                int nx = car.x + dx[nd];
                int ny = car.y + dy[nd];
                if (road[nx][ny] == 1 || visited[nx][ny]) {
                    continue;
                }

                car = new Car(nx, ny, nd);
                visited[nx][ny] = true;
                flag = true;
                dist++;
                break;
            }

            if (flag) {
                continue;
            }

            int nx = car.x - dx[nd];
            int ny = car.y - dy[nd];

            if (road[nx][ny] == 1) {
                break;
            }

            car = new Car(nx, ny, nd);
        }

        System.out.println(dist);
    }

    private static class Car {

        int x, y, d;

        public Car(int x, int y, int d) {
            this.x = x;
            this.y = y;
            this.d = d;
        }
    }
}