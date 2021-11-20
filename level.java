package coolassic;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
public class level {
	byte[] ldat;
	int[] dims = new int[3];
	public static level lvl;
	public static byte curp;
	public static boolean[] pavv = new boolean[(player.plamnt + 1)];
	public static boolean[] sltak = new boolean[(player.plamnt + 1)];
	public level(int x, int y, int z) {
		x = x * 16;
		y = y * 16;
		z = z * 16;
		ldat = new byte[(x * y * z) + 4];
		dims[0] = x;
		dims[1] = y;
		dims[2] = z;
		byte[] levelsize = new byte[4];
		levelsize = ByteBuffer.allocate(4).putInt(x * y * z).array();
		for (byte t = 0; t<4; t++) {
			ldat[t] = levelsize[t];
		}
	}
	public synchronized byte[] raw() {
		return ldat;
	}
	public synchronized void genfloor(byte content) {
		for (byte n = 0; n<dims[0]; n++) {
			for (byte o = 0; o<dims[2]; o++) {
				ldat[(n * dims[2]) + o + 4] = content;
			}
		}
	}
	public synchronized void insert(byte blockid, int x, int y, int z) {
		ldat[(y * dims[0] * dims[2]) + (z * dims[0]) + x + 4] = blockid;
	}
	public synchronized void place(byte blockid, short x, short y, short z, byte plyid) {
		if ((x < 0) || (y < 0) || (z < 0) || (x >= dims[0]) || (y >= dims[1]) || (z >= dims[2])) {
			return;
		}
		boolean[] perms = player.plist[plyid].solid((byte) 0);
		if (blockid == 1 && perms[0] && perms[1]) {
			blockid = 7;
		}
		byte[] blp = new byte[8];
		blp[0] = 6;
		byte[] leng = new byte[2];
		leng = ByteBuffer.allocate(2).putShort(x).array();
		blp[1] = leng[0];
		blp[2] = leng[1];
		leng = ByteBuffer.allocate(2).putShort(y).array();
		blp[3] = leng[0];
		blp[4] = leng[1];
		leng = ByteBuffer.allocate(2).putShort(z).array();
		blp[5] = leng[0];
		blp[6] = leng[1];
		blp[7] = blockid;
		if (!perms[1] && (blockid == 0)) {
			if (retrieve(x, y, z) == 7) {
				blp[7] = 7;
				try {
					player.plist[plyid].plout.write(blp);
				}
				catch (Exception exc) {
					System.out.println("level error: " + exc);
				}
				return;
			}
		}
		ldat[(y * dims[0] * dims[2]) + (z * dims[0]) + x + 4] = blockid;
		for (byte t = 0; t < (player.plamnt + 1); t++) {
			if (pavv[t]) {
				try {
					player.plist[t].plout.write(blp);
				}
				catch (Exception exc) {
					System.out.println("level error: " + exc);
				}
			}
		}
	}
	public static void getplayers(byte plyid) {
		byte[] nma = new byte[64];
		byte[] mesn = new byte[66];
		byte[] plsp = new byte[74];
		byte[] pldat = new byte[8];
		String namp = new String(player.plist[plyid].name);
		nma = namp.getBytes(StandardCharsets.US_ASCII);
		byte pleng = 0;
		for (byte u = 63; u>=0; u--) {
			pleng = u;
			if (player.plist[plyid].name[u] != ' ') {
				pleng = (byte) (u+1);
				break;
			}
		}
		mesn[0] = 0x0d;
		mesn[1] = -1;
		for (byte i = 0; i < 64; i++) {
			mesn[i+2] = nma[i];
		}
		String jtg = new String(" joined the game");
		byte[] jtgb = jtg.getBytes(StandardCharsets.US_ASCII);
		for (byte i = 0; (i < (64 - pleng)) && (i < 16); i++) {
			mesn[pleng + i + 2] = jtgb[i];
		}
		for (byte t = 0; t < (player.plamnt + 1); t++) {
			if (pavv[t] & (t != plyid)) {
				pldat = (player.plist[t].data());
				String nas = new String(player.plist[t].name);
				nma = nas.getBytes(StandardCharsets.US_ASCII);
				plsp[0] = 7;
				plsp[1] = t;
				for (byte u = 0; u<64; u++) {
					plsp[u+2] = nma[u];
				}
				try {
				player.plist[t].plout.write(mesn);
				}
				catch (Exception exc) {
					System.out.println("level error: " + exc);
				}
				for (byte u = 0; u<8; u++) {
					plsp[u+66] = pldat[u];
				}
				try {
					player.plist[plyid].plout.write(plsp);
				}
				catch (Exception exc) {
					System.out.println("level error: " + exc);
				}
			}
		}
	}
	public static void introplayer(byte plyid) {
		byte[] nma = new byte[64];
		byte[] plsp = new byte[74];
		String nas = new String(player.plist[plyid].name);
		nma = nas.getBytes(StandardCharsets.US_ASCII);
		plsp[0] = 7;
		plsp[1] = plyid;
		for (byte u = 0; u<64; u++) {
			plsp[u+2] = nma[u];
		}
		byte[] pldat = new byte[8];
		pldat = (player.plist[plyid].data());
		for (byte u = 0; u<8; u++) {
			plsp[u+66] = pldat[u];
		}
		for (byte t = 0; t<(player.plamnt + 1); t++) {
			if (pavv[t] & (t != plyid)) {
				try {
					player.plist[t].plout.write(plsp);
				}
				catch (Exception exc) {
					System.out.println("level error: " + exc);
				}
			}
		}
	}
	public static void intromovement(byte plyid, int chx, int chy, int chz, byte oyaw, byte yaw, byte opitch, byte pitch, short tx, short ty, short tz) {
		boolean tptn = false;
		if ((chx != 0) | (chy != 0) | (chz != 0) | ((yaw - oyaw) != 0) | ((pitch - opitch) != 0)) {
			byte[] mover = new byte[7];
			if ((chx < -128) || (chx > 127) || (chy < -128) || (chy > 127) || (chz < -128) || (chz > 127)) {
				tptn = true;
				byte[] telp = new byte[10];
				byte[] leng = new byte[2];
				leng = ByteBuffer.allocate(2).putShort(tx).array();
				telp[2] = leng[0];
				telp[3] = leng[1];
				leng = ByteBuffer.allocate(2).putShort(ty).array();
				telp[4] = leng[0];
				telp[5] = leng[1];
				leng = ByteBuffer.allocate(2).putShort(tz).array();
				telp[6] = leng[0];
				telp[7] = leng[1];
				telp[8] = oyaw;
				telp[9] = opitch;
				telp[1] = -1;
				telp[0] = 8;
				telp[1] = plyid;
				for (byte i = 0; i < (player.plamnt + 1); i++) {
					if ((level.pavv[i]) && (i != plyid)) {
						try {
							player.plist[i].plout.write(telp);
						}
						catch (Exception exc) {
							System.out.println("level error - error sending player location load teleportation packet: " + exc);
						}
					}
				}
			}
			else if (((chx != 0) || (chy != 0) || (chz != 0)) && ((yaw - oyaw) == 0) && ((pitch - opitch) == 0)) {
				mover = new byte[5];
				mover[0] = 0x0a;
				mover[1] = plyid;
				mover[2] = (byte) chx;
				mover[3] = (byte) chy;
				mover[4] = (byte) chz;
			}
			else if (((chx == 0) && (chy == 0) && (chz == 0)) && (((yaw - oyaw) != 0) || ((pitch - opitch) != 0))) {
				mover = new byte[4];
				mover[0] = 0x0b;
				mover[1] = plyid;
				mover[2] = yaw;
				mover[3] = pitch;
			}
			else if (((chx != 0) || (chy != 0) || (chz != 0)) && (((yaw - oyaw) != 0) || ((pitch - opitch) != 0))) {
				mover = new byte[7];
				mover[0] = 0x09;
				mover[1] = plyid;
				mover[2] = (byte) chx;
				mover[3] = (byte) chy;
				mover[4] = (byte) chz;
				mover[5] = yaw;
				mover[6] = pitch;
			}
			if (!tptn) {
				for (byte t = 0; t<(player.plamnt + 1); t++) {
					if (pavv[t] & (t != plyid)) {
						try {
							player.plist[t].plout.write(mover);
						}
						catch (Exception exc) {
							System.out.println("level error - error sending player movement packet: " + exc);
						}
					}
				}
			}
		}
	}
	public static void broadcast(byte tplyid, byte[] message, byte fplyid) {
		byte[] mess = new byte[66];
		try {
			byte pleng = 0;
			if (fplyid != -1) {
				for (byte t = 63; t>=0; t--) {
					pleng = t;
					if (player.plist[fplyid].name[t] != ' ') {
						pleng = (byte) (t+1);
						break;
					}
				}
			}
			byte[] pnb = new byte[pleng];
			char[] nac = new char[pleng];
			byte r = 0;
			if (fplyid != -1) {
				while (r < pleng) {
					nac[r] = player.plist[fplyid].name[r];
					r++;
				}
			}
			String names = new String(nac);
			pnb = names.getBytes(StandardCharsets.US_ASCII);
			for (byte t = 0; t<pleng; t++) {
				mess[t+2] = pnb[t];
			}
			if (fplyid != -1) {
				if (pleng<64) {
					mess[pleng + 2] = 58;
					if (pleng<63) {
						mess[pleng + 3] = 32;
					}
				}
			}
			mess[0] = 0x0d;
			mess[1] = fplyid;
			if (fplyid != -1 ) {
				for (byte t = (byte) (pleng + 2); t<64; t++) {
					mess[t + 2] = message[t - pleng];
				}
			}
			else {
				for (byte t = (byte) (pleng + 2); t<66; t++) {
					mess[t] = message[t];
				}
			}
			if (tplyid == -1) {
				for (byte t = 0; t < (player.plamnt + 1); t++) {
					if (pavv[t]) {
						player.plist[t].plout.write(mess);
					}
				}
			} else if (tplyid < (player.plamnt + 1)) {
				if (pavv[tplyid]) {
					player.plist[tplyid].plout.write(mess);
				}
			}
		}
		catch (Exception exc) {
			System.out.println("error sending message: " + exc);
		}
	}
	public static void msg(byte tplyid, String message) {
		byte[] messb;
		messb = message.getBytes(StandardCharsets.US_ASCII);
		byte[] messf = new byte[66];
		messf[0] = 0x0d;
		messf[1] = tplyid;
		boolean exit = false;
		int lread = 0;
		int extl = 0;
		boolean ext = false;
		while (!exit) {
			for (byte i = 0; i < 64; i++) {
				messf[i + 2] = 32;
			}
			for (byte i = 0; i < 64; i++) {
				if (i < ((messb.length) - (64 * lread + extl))) {
					if (messb[extl + i + (64 * lread)] == 10) {
						extl += i + 1;
						ext = true;
						break;
					}
					messf[i + 2] = messb[extl + i + (64 * lread)];
				}
			}
			broadcast(tplyid, messf, (byte) -1);
			if (!ext) {
				lread += 1;
			}
			ext = false;
			if (messb.length <= (64 * lread + extl)) {
				exit = true;
			}
		}
	}
	public synchronized byte retrieve(int x, int y, int z) {
		byte blockid;
		blockid = ldat[(y * dims[0] * dims[2]) + (z * dims[0]) + x + 4];
		return blockid;
	}
	public int sizeInBytes() {
		return ((dims[0] * dims[1] * dims[2]) + 4);
	}
}
