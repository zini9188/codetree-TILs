import java.io.*;
import java.util.StringTokenizer;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static int N, M, P, C, D;
    static int[][] map;
    static Point rudolf;
    static Point[] santas;
    static SantaStatus[] santaStatuses;
    static int overSantaCount;
    static int turn;

    // 상우하좌 // 대각선
    static int[] dr = {-1, 0, 1, 0, -1, -1, 1, 1}, dc = {0, 1, 0, -1, -1, 1, -1, 1};

    public static void main(String[] args) throws IOException {
        st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());

        // 맵 초기화
        map = new int[N + 1][N + 1];

        // 루돌프
        st = new StringTokenizer(br.readLine());
        int rr = Integer.parseInt(st.nextToken());
        int rc = Integer.parseInt(st.nextToken());
        rudolf = new Point(rr, rc);
        map[rr][rc] = -1;

        // 산타들 입력
        // 맵에 산타들 1~P번까지 숫자로 기입
        santas = new Point[P + 1];
        santaStatuses = new SantaStatus[P + 1];
        for (int i = 1; i <= P; i++) {
            st = new StringTokenizer(br.readLine());
            int pn = Integer.parseInt(st.nextToken());
            int sr = Integer.parseInt(st.nextToken());
            int sc = Integer.parseInt(st.nextToken());
            santas[pn] = new Point(sr, sc);
            map[sr][sc] = pn;
            santaStatuses[pn] = new SantaStatus(0, 0, false);
        }

        overSantaCount = 0;
        turn = 1;

        while (!GameOver()) {
            moveRudolf();
            moveAllSanta();
            turn++;
        }

        for (int i = 1; i <= P; i++) {
            System.out.print(santaStatuses[i].score + " ");
        }
    }

    static void moveAllSanta() {
        for (int p = 1; p <= P; p++) {
            // 탈락하면 넘어감
            SantaStatus santaStatus = santaStatuses[p];
            if (santaStatus.isOver || !santaStatus.canMove()) {
                continue;
            }

            moveSanta(p);
        }

        for (int p = 1; p <= P; p++) {
            SantaStatus santaStatus = santaStatuses[p];
            if (!santaStatus.isOver) {
                santaStatus.score++;
            }
        }
    }

    static void moveSanta(int index) {
        Point santa = santas[index];
        int currentDist = calc(santa, rudolf);
        int minDist = currentDist;
        int dir = 0;
        Point temp = new Point(0, 0);

        for (int d = 0; d < 4; d++) {
            int nr = santa.r + dr[d];
            int nc = santa.c + dc[d];

            // 다른산타, 게임판 밖은 움직일수없음
            if (outRange(nr, nc) || map[nr][nc] > 0) {
                continue;
            }

            // 다음 좌표에서 루돌프와의 거리 계산
            int dist = calc(new Point(nr, nc), rudolf);
            // 만약 가까워지면 움직임
            if (minDist > dist) {
                temp.r = nr;
                temp.c = nc;
                minDist = dist;
                dir = d;
            }
        }

        // 산타가 가까워진다면
        if (currentDist > minDist) {
            // 현재 산타를 맵에서 없앤다.
            map[santa.r][santa.c] = 0;

            // 해당 칸에 루돌프가 있는지 확인한다.
            if (map[temp.r][temp.c] == -1) {
                // 있다면 충돌하며 D만큼 점수 얻는다. 산타는 기절
                SantaStatus santaStatus = santaStatuses[index];
                santaStatus.score += D;
                santaStatus.stun(turn);

                // 해당 산타는 이동한 방향의 반대로 D칸 밀려난다.
                dir = (dir + 2) % 4;
                int nr = temp.r + dr[dir] * D;
                int nc = temp.c + dc[dir] * D;

                // 밀린 곳이 밖이면 탈락한다.
                if (outRange(nr, nc)) {
                    santaStatus.isOver = true;
                    overSantaCount++;
                    return;
                }

                // 밀린 곳에 산타가 있으면 다 밀어버린다.
                if (map[nr][nc] > 0) {
                    chain(santas[map[nr][nc]], dir, map[nr][nc]);
                }

                // 밀린 곳에 산타 넣는다.
                map[nr][nc] = index;
                santas[index].r = nr;
                santas[index].c = nc;
            } else {
                // 루돌프 없으면 산타 배치한다.
                map[temp.r][temp.c] = index;
                santas[index].setPoint(temp);
            }
        }
    }

    static void moveRudolf() {
        int dist = Integer.MAX_VALUE;
        Point p = new Point(0, 0);
        for (int i = 1; i <= P; i++) {
            // 탈락한 산타면 넘어감
            if (santaStatuses[i].isOver) {
                continue;
            }

            // 루돌프와 산타와의 거리 측정
            int d = calc(rudolf, santas[i]);
            // 가장 가까운 거리로 갱신
            if (d <= dist) {
                if (d < dist) {
                    dist = d;
                    p = new Point(0, 0);
                }
                if (p.compareTo(santas[i]) > 0) {
                    p.setPoint(santas[i]);
                }
            }
        }

        int d = Integer.MAX_VALUE;
        int dir = -1;
        Point temp = new Point(rudolf.r, rudolf.c);
        for (int i = 0; i < 8; i++) {
            int nx = rudolf.r + dr[i];
            int ny = rudolf.c + dc[i];

            if (outRange(nx, ny)) {
                continue;
            }

            // p에 가장 가까운 위치로 이동
            int dd = calc(p, new Point(nx, ny));

            if (d > dd) {
                d = dd;
                temp = new Point(nx, ny);
                dir = i;
            }
        }

        // 루돌프 이동
        map[rudolf.r][rudolf.c] = 0;
        rudolf.setPoint(temp);

        // 이동한 곳에 산타 있는 경우
        int santaIdx = map[rudolf.r][rudolf.c];
        // C만큼의 점수, 산타는 밀려남
        if (santaIdx > 0) {
            SantaStatus santaStatus = santaStatuses[santaIdx];
            santaStatus.score += C;
            santaStatus.stun(turn);

            int nx = santas[santaIdx].r + dr[dir] * C;
            int ny = santas[santaIdx].c + dc[dir] * C;

            if (outRange(nx, ny)) {
                santaStatus.isOver = true;
                overSantaCount++;
            } else {
                if (map[nx][ny] > 0) {
                    chain(santas[map[nx][ny]], dir, map[nx][ny]);
                }

                // 산타 위치 설정
                santas[santaIdx].r = nx;
                santas[santaIdx].c = ny;
                map[nx][ny] = santaIdx;
            }
        }

        // 맵에 루돌프 표시
        map[rudolf.r][rudolf.c] = -1;
    }

    static void chain(Point santa, int dir, int idx) {
        int nx = santa.r + dr[dir];
        int ny = santa.c + dc[dir];

        // 밀려나서 탈락
        if (outRange(nx, ny)) {
            santaStatuses[idx].isOver = true;
            overSantaCount++;
        } else {
            // 산타 있으면 하나 밈
            if (map[nx][ny] > 0) {
                chain(santas[map[nx][ny]], dir, map[nx][ny]);
            }

            // 다음 위치에 산타 표시
            map[nx][ny] = idx;
            santas[idx].r = nx;
            santas[idx].c = ny;
        }
    }

    // 턴이 M보다 크거나 모든 산타가 탈락하면
    static boolean GameOver() {
        return turn > M || overSantaCount >= P;
    }

    static boolean outRange(int r, int c) {
        return r < 1 || c < 1 || r > N || c > N;
    }

    static int calc(Point a, Point b) {
        return (int) (Math.pow(Math.abs(a.r - b.r), 2) + Math.pow(Math.abs(a.c - b.c), 2));
    }

    static class Point implements Comparable<Point> {

        int r, c;

        public Point(int r, int c) {
            this.r = r;
            this.c = c;
        }

        public void setPoint(Point p) {
            this.r = p.r;
            this.c = p.c;
        }

        @Override
        public int compareTo(Point o) {
            if (this.r == o.r) {
                return o.c - this.c;
            }
            return o.r - this.r;
        }

        @Override
        public String toString() {
            return "Point{" + "r=" + r + ", c=" + c + '}';
        }
    }

    // 산타의 상태를 기록하는 클래스
    // 점수, 움직일 수 있는 턴, 게임 오버 여부
    static class SantaStatus {

        int score, canMoveTurn;
        boolean isOver;

        public SantaStatus(int score, int canMoveTurn, boolean isOver) {
            this.score = score;
            this.canMoveTurn = canMoveTurn;
            this.isOver = isOver;
        }

        // 현재 턴에 하나 플러스
        public void stun(int k) {
            canMoveTurn = k + 1;
        }

        // 현재 턴보다 작으면 움직일 수 있음
        public boolean canMove() {
            return canMoveTurn < turn;
        }
    }
}