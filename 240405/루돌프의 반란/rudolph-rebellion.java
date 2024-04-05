// 1.게임판
// N*N크기의 격자, (1,1) 좌상단
// M 개의 텀에 걸쳐 진행
// M개의 턴에 걸쳐 진행
// 매 턴마다 루돌프와 산타는 한 번씩 움직임
// 루돌프가 움직이고, 1~P번 산타까지 순서대로 움직임
// 기절하거나 격자 밖으로 나가 탈락한 산타는 움직일 수 없음
// 게임판에서 두 칸의 기리는 (r1-r2)^2 + (c1 - c2)^2

// 2 루돌프 움직임
// 가장 가까운 산타를 향해 1칸 돌진, 게임에서탈락하지 않은 산타중 가장 가까운 산타
// 만약 가장 가까운 산타가 2명 이상이면
// r좌표가 큰 산타에게 돌진 r이 동일하면 c
// 루돌프는 8방향으로 돌진 가능
// 가장 우선순위가 높은 산타를 향해 8방향중 가장 가까워지는 방향으로 한 칸 돌진
// -> 방향을 우선순위대로 탐색

// 3. 산타의 움직임
// 산타는 1~P번까지 순서대로 움직
// 기절하거나 탈락한 산타 움직일수 없음
// 루돌프에게 가장 가까워지는 방향으로 1칸 이동
// 산타는 다른 산타가 있는 칸이나 게임판 밖으로는 움직일 수 없음
// 움직일 수 있는 칸이 없다면 산타는 움직이지 않음
// 움직일 수 있는 칸이 있더라도 루돌프로부터 가까워질 수 없다면 움직이지 않음
// 산타는 4방향으로만 움직임, 여러개면 상우하좌우선순위

// 4. 충돌
// 산타와 루돌프가 같은 칸이면 충돌 발생
// 루돌프가 움직여서 충돌이 일어난 경우 산타는 C만큼의 점수,
// 이와 동시에 산타는 루돌프가 이동한 방향으로 C칸 밀림
// 산타가 움직여서 충돌이 일어나면 D만큼의 점수
// 반대 방향으로 D칸만큼 밀림
// 밀려나는 것은 충돌 없고 정확히 원하는 위치에 도달
// 밀려난곳이 밖이면 탈락
// 다른 산타가 있으면 상호 작용

// 5. 루돌프와 충돌 후 착지하는 칸에서만 상호작용
// 충돌 후 착지하는 칸에 다른 산타 있으면 1칸 해당 방향으로 밀림
// 옆에 산타가 있으면 연쇄적으로 밀림
// 게임판 밖으로 밀리면 탈락

// 6. 기절
// k번째 턴이라면 K + 1번째 턴까지 기절
// K+2번째 턴부턴 다시 정상 상태
// 기절한 산타는 움직일 수 없음
// 루돌프는 기절한 산타를 돌진 대상으로 선택 가능

// 게임 종료
// M번 턴이 지나면 게임 종료
// P명의 산타 모두 탈락하면 즉시 종료
// 탈락하지 않은 산타에게는 1점을 추가로 부여

import java.io.*;
import java.util.StringTokenizer;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static int N, M, P, C, D;
    static int[][] map;
    static Point animal;
    static Point[] santas;
    static SantaStatus[] santaStatuses;
    static int overSantaCount;
    static int turn;

    // 상우하좌 // 대각선
    static int[] dx = {-1, 0, 1, 0, -1, -1, 1, 1}, dy = {0, 1, 0, -1, -1, 1, -1, 1};

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
        animal = new Point(rr, rc);
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
            moveAnimal();
            moveSantas();
            turn++;
        }

        for (int i = 1; i <= P; i++) {
            System.out.print(santaStatuses[i].score + " ");
        }
    }

    private static void moveSantas() {
        for (int i = 1; i <= P; i++) {
            // 탈락하면 넘어감
            if (santaStatuses[i].isOver) {
                continue;
            }

            if (!santaStatuses[i].canMove(turn)) {
                continue;
            }

            moveSanta(i);
        }

        for (int i = 1; i <= P; i++) {
            if (!santaStatuses[i].isOver) {
                santaStatuses[i].score++;
            }
        }
    }

    private static void moveSanta(int idx) {
        Point santa = santas[idx];
        int currentDist = calc(santa, animal);
        int minDist = currentDist;
        int dir = 0;
        Point temp = new Point(0, 0);

        for (int i = 0; i < 4; i++) {
            int nr = santa.r + dx[i];
            int nc = santa.c + dy[i];

            // 다른산타, 게임판 밖은 움직일수없음
            if (outRange(nr, nc) || map[nr][nc] > 0) {
                continue;
            }

            // 다음 좌표에서 루돌프와의 거리 계산
            int dist = calc(new Point(nr, nc), animal);
            // 만약 가까워지면 움직임
            if (minDist > dist) {
                temp.r = nr;
                temp.c = nc;
                minDist = dist;
                dir = i;
            }
        }

        // 산타가 가까워진다면
        if (currentDist > minDist) {
            // 현재 산타를 맵에서 없앤다.
            map[santa.r][santa.c] = 0;

            // 해당 칸에 루돌프가 있는지 확인한다.
            if (map[temp.r][temp.c] == -1) {
                // 있다면 충돌하며 D만큼 점수 얻는다.
                santaStatuses[idx].score += D;
                // 산타 기절
                santaStatuses[idx].stun(turn);

                // 해당 산타는 이동한 방향의 반대로 D칸 밀려난다.
                dir += 2;
                dir %= 4;

                int nx = temp.r + dx[dir] * D;
                int ny = temp.c + dy[dir] * D;

                // 밀린 곳이 밖이면 탈락한다.
                if (outRange(nx, ny)) {
                    santaStatuses[idx].isOver = true;
                    overSantaCount++;
                    return;
                }

                // 밀린 곳에 산타가 있으면 다 밀어버린다.
                if (map[nx][ny] > 0) {
                    chain(santas[map[nx][ny]], dir, map[nx][ny]);
                }
                // 밀린 곳에 산타 넣는다.
                map[nx][ny] = idx;
                santas[idx].r = nx;
                santas[idx].c = ny;
            } else {
                // 루돌프 없으면 산타 배치한다.
                map[temp.r][temp.c] = idx;
                santas[idx].setPoint(temp);
            }
        }
    }

    private static void moveAnimal() {
        int dist = Integer.MAX_VALUE;
        Point p = new Point(0, 0);
        for (int i = 1; i <= P; i++) {
            // 탈락한 산타면 넘어감
            if (santaStatuses[i].isOver) {
                continue;
            }

            // 루돌프와 산타와의 거리 측정
            int d = calc(animal, santas[i]);
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
        Point temp = new Point(animal.r, animal.c);
        for (int i = 0; i < 8; i++) {
            int nx = animal.r + dx[i];
            int ny = animal.c + dy[i];

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
        map[animal.r][animal.c] = 0;
        animal.setPoint(temp);

        // 이동한 곳에 산타 있는 경우
        int santaIdx = map[animal.r][animal.c];
        // C만큼의 점수, 산타는 밀려남
        if (santaIdx > 0) {
            santaStatuses[santaIdx].score += C;
            santaStatuses[santaIdx].stun(turn);

            int nx = santas[santaIdx].r + dx[dir] * C;
            int ny = santas[santaIdx].c + dy[dir] * C;

            if (outRange(nx, ny)) {
                santaStatuses[santaIdx].isOver = true;
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
        map[animal.r][animal.c] = -1;
    }

    static void chain(Point santa, int dir, int idx) {
        int nx = santa.r + dx[dir];
        int ny = santa.c + dy[dir];

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

        int score;
        int canMove;
        boolean isOver;

        public SantaStatus(int score, int canMove, boolean isOver) {
            this.score = score;
            this.canMove = canMove;
            this.isOver = isOver;
        }

        // 현재 턴에 하나 플러스
        public void stun(int k) {
            canMove = k + 1;
        }

        // 현재 턴보다 작으면 움직일 수 있음
        public boolean canMove(int k) {
            return canMove < k;
        }
    }
}