import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    // 몬스터의 마리 수 m, 진행되는 턴의 수 t
    static int m, t;
    // 상하좌우의 우선순위를 가지기 때문에, 우좌하상의 우선순위로 검사하며 최대값 갱신
    static int[] ddx = {-1, -1, 0, 1, 1, 1, 0, -1}, ddy = {0, -1, -1, -1, 0, 1, 1, 1};
    static List<Monster>[][] aliveMonsters;
    static int[][] deadMonsters;
    static List<Egg>[][] eggs;
    static Pacman pacman;

    public static void main(String[] args) throws Exception {
        init();
        simulate();
    }

    private static void simulate() {

        for (int i = 1; i <= t; i++) {
            tryDuplicateMonster();
            moveMonsters(i);
            pacman.move(i);
            duplicatedMonsters();
        }

        int ans = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                ans += aliveMonsters[i][j].size();
            }
        }
        System.out.println(ans);
    }

    // 몬스터의 복제를 시도
    // 현재 위치에서 자신과 같은 방향을 가진 몬스터를 복제
    // 복제된 몬스터는 부화되지 않은 상태로 움직이지 못함
    // 알의 형태를 띈 복제 몬스터는 현재 시점을 기준으로 각 몬스터와 동일한 방향
    // 이후 이 알이 부화할 시 해당 방향을 지닌 채로 깸
    private static void tryDuplicateMonster() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (Monster monster : aliveMonsters[i][j]) {
                    eggs[i][j].add(new Egg(monster));
                }
            }
        }
    }

    // 몬스터 이동
    // 몬스터들은 현재 자신이 가진 방향대로 한 칸 이동.
    // 움직이려는 칸에 몬스터 시체가 있거나, 팩맨이 있거나, 격자를 벗어나면
    // 반시계방향으로 45도를 회전하고 다시 판단
    // 가능할 때까지 판단후에, 8방향 모두 움직일 수 없다면, 움직이지 않음
    private static void moveMonsters(int turn) {
        Queue<Monster> queue = new ArrayDeque<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int size = aliveMonsters[i][j].size() - 1; size >= 0; size--) {
                    Monster move = aliveMonsters[i][j].get(size).move(turn);
                    queue.add(move);
                }
            }
        }

        while (!queue.isEmpty()) {
            Monster monster = queue.poll();
            aliveMonsters[monster.x][monster.y].add(monster);
        }
    }

    // 팩맨은 총 3칸을 이동
    // 64개의 이동방법중 가장 많이 몬스터를 먹을 수 있는 방향으로 움직임
    // 상좌하우의 우선순위
    // 격자 바깥을 나가면 고려하지 않음
    // 팩맨을 알은 먹지 않고, 움직이전에 함께 있던 몬스터도 먹지 않음
    // 이동하는 과정에서의 몬스터만 먹음
    // 이때 몬스터를 먹으면 시체를 남김.

    // 몬스터 복제 완성
    // 알 형태의 몬스터 부화
    // 복제가 된 몬스터의 방향을 지닌 채로 깨어남
    private static void duplicatedMonsters() {
        for (List<Egg>[] egg : eggs) {
            for (List<Egg> eggList : egg) {
                for (int i = eggList.size() - 1; i >= 0; i--) {
                    Monster monster = eggList.get(i).wakeUp();
                    aliveMonsters[monster.x][monster.y].add(monster);
                }
            }
        }
    }

    private static void init() throws IOException {
        st = new StringTokenizer(br.readLine());
        m = IPS();
        t = IPS();

        // 4x4 크기의 격자 몬스터들이 들어있음
        aliveMonsters = new List[4][4];
        deadMonsters = new int[4][4];
        eggs = new List[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                aliveMonsters[i][j] = new ArrayList<>();
                eggs[i][j] = new ArrayList<>();
            }
        }

        // 팩면 격자에서의 초기 위치 r,c
        st = new StringTokenizer(br.readLine());
        int r = Integer.parseInt(st.nextToken()) - 1;
        int c = Integer.parseInt(st.nextToken()) - 1;
        pacman = new Pacman(r, c);
        // m개의 줄에는 몬스터의 위치 r,c, 방향정보 d
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            r = Integer.parseInt(st.nextToken()) - 1;
            c = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken()) - 1;
            aliveMonsters[r][c].add(new Monster(r, c, d));
        }
    }

    static int IPS() {
        return Integer.parseInt(st.nextToken());
    }

    static class Pacman {

        int[] dx = {0, 1, 0, -1}, dy = {1, 0, -1, 0};
        int x, y;

        public Pacman(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move(int turn) {
            int key = -1, eat = -1;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < 4; k++) {
                        int nx1 = x + dx[i];
                        int ny1 = y + dy[i];
                        int nx2 = nx1 + dx[j];
                        int ny2 = ny1 + dy[j];
                        int nx3 = nx2 + dx[k];
                        int ny3 = ny2 + dy[k];

                        // i,j,k에 대해 이동한 방향이 밖이면
                        if (outRange(nx1, ny1) || outRange(nx2, ny2) || outRange(nx3, ny3)) {
                            continue;
                        }

                        int count = aliveMonsters[nx1][ny1].size() + aliveMonsters[nx2][ny2].size();
                        if (nx1 != nx3 || ny1 != ny3) {
                            count += aliveMonsters[nx3][ny3].size();
                        }

                        // 갱신 가능하면 갱신
                        if (count >= eat) {
                            eat = count;
                            key = (k + 1) * 100 + (j + 1) * 10 + (i + 1);
                        }
                    }
                }
            }

            if (key == -1) {
                return;
            }

            // key로 팩맨 움직임
            while (key > 0) {
                int dir = key % 10 - 1;
                pacman.moveDir(dir);
                if(!aliveMonsters[pacman.x][pacman.y].isEmpty()){
                    deadMonsters[pacman.x][pacman.y] = turn + 2;
                }
                aliveMonsters[pacman.x][pacman.y].clear();
                key /= 10;
            }
        }

        private void moveDir(int dir) {
            this.x += dx[dir];
            this.y += dy[dir];
        }
    }

    // 몬스터는 죽을 수 있다.
    // 회전을 한다.
    // 움직인다.
    // 시체는 2턴 뒤에 사라진다
    // 몬스터는 알 상태가 있다.
    static class Monster {

        int x, y, d;

        public Monster(int x, int y, int d) {
            this.x = x;
            this.y = y;
            this.d = d;
        }

        public Monster move(int turn) {
            for (int i = 0; i < 8; i++) {
                int nx = x + ddx[(d + i) % 8];
                int ny = y + ddy[(d + i) % 8];
                // 격자밖, 시체있음, 팩맨있음 넘어감
                if (outRange(nx, ny) || deadMonsters[nx][ny] >= turn ||
                        (nx == pacman.x && ny == pacman.y)) {
                    continue;
                }     // 갈 수 있는 경우
                // 해당 객체 지우고
                aliveMonsters[x][y].remove(this);
                x = nx;
                y = ny;
                d = (d + i) % 8;
                return this;
            }
            aliveMonsters[x][y].remove(this);
            return this;
        }
    }

    static boolean outRange(int x, int y) {
        return x < 0 || y < 0 || x >= 4 || y >= 4;
    }

    static class Egg {

        int x, y, d;

        public Egg(Monster monster) {
            this.x = monster.x;
            this.y = monster.y;
            this.d = monster.d;
        }

        public Monster wakeUp() {
            eggs[x][y].remove(this);
            return new Monster(x, y, d);
        }
    }
}