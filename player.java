package coolassic;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class player implements Runnable {
	char[] name = new char[64];
	byte pvn;
	char[] verif = new char[64];
	byte id;
	Socket sock;
	InputStream plin;
	DataOutputStream plout;
	short x;
	short y;
	short z;
	byte yaw;
	byte pitch;
	byte chatperms;
	public static player[] plist = new player[16];
	public player(byte prot, int pf, Socket socke, byte ident) {
		pvn = prot;
		sock = socke;
		id = ident;
		chatperms = 0;
	}
	public static byte donothing() {
		return (byte) 1;
	}
	public void run() {}
	public void play() {
		level.curp++;
		player.plist[(level.curp - 1)].playe();
	}
	public void playe() {
		try {
			System.out.println("New player joined, id:" + id + ", connection:" + sock);
			byte[] condar = new byte[131];
			int conds;
			String servername = new String("testingServer                                                   ");
			String motd = new String("029d841198a9a400a949beda04eb460614bd46ec0dff01cdd21252d8f9b3c10c");
			byte[] serb = new byte[64];
			byte[] motb = new byte[64];
			char[] blankchars = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
			this.plin = this.sock.getInputStream();
			this.plout = new DataOutputStream(this.sock.getOutputStream());
			this.name = blankchars;
			this.verif = blankchars;
			byte[] clie = new byte[131];
			this.plin.read(condar);
			if (condar[0] == 0) {
				System.out.print("Client is connecting with protocol version " + condar[1]);
				if (condar[1] == 7) {
					System.out.println(" (late classic)");
				}
				this.name = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
				this.verif = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
				for (byte n = 0; n<64; n++) {
					this.name[n] = (char) condar[n+2];
					this.verif[n] = (char) condar[n+66];
				}
				this.pvn = condar[1];
				System.out.print("username is:");
				System.out.print(this.name);
				System.out.println("");
				System.out.print("key is:");
				System.out.println(this.verif);
				System.out.println("Server name:" + servername);
				System.out.println("MOTD:" + motd);
				serb = servername.getBytes(StandardCharsets.US_ASCII);
				motb = motd.getBytes(StandardCharsets.US_ASCII);
				clie[1] = 0x07;
				clie[130] = 0x64;
				for (byte n = 0; n<64; n++) {
					clie[n+2] = serb[n];
					clie[n+66] = motb[n];
				}
				this.plout.write(clie);
				System.out.println("");
				System.out.println("Sent server name and MOTD to client");
			}
			ByteArrayOutputStream gzstr = new ByteArrayOutputStream();
			GZIPOutputStream levelgz = new GZIPOutputStream(gzstr);
			levelgz.write(level.lvl.raw());
			levelgz.close();
			gzstr.close();
			byte[] gzlev;
			gzlev = gzstr.toByteArray();
			byte[] parr = new byte[] {0x01};
			if (gzlev.length>1024) {
				System.out.println("error: gzipped level is over 1024 bytes, this function is not supported yet");
				System.exit(1);
			}
			System.out.println("Level made");
			byte[] levgz = new byte[1028];
			int c = 0;
			while (c<gzlev.length) {
				levgz[c + 3] = gzlev[c];
				c++;
			}
			byte[] levdecl = new byte[1];
			levdecl[0] = 0x02;
			this.plout.write(levdecl);
			byte[] leng = new byte[2];
			Short gzlens = (short) gzlev.length;
			leng = ByteBuffer.allocate(2).putShort(gzlens).array();
			levgz[0] = 0x03;
			levgz[1] = leng[0];
			levgz[2] = leng[1];
			levgz[1027] = 0x64;
			this.plout.write(levgz);
			byte[] lvf = new byte[7];
			lvf[0] = 0x04;
			leng = ByteBuffer.allocate(2).putShort((short) level.lvl.dims[0]).array();
			lvf[1] = leng[0];
			lvf[2] = leng[1];
			leng = ByteBuffer.allocate(2).putShort((short) level.lvl.dims[1]).array();
			lvf[3] = leng[0];
			lvf[4] = leng[1];
			leng = ByteBuffer.allocate(2).putShort((short) level.lvl.dims[2]).array();
			lvf[5] = leng[0];
			lvf[6] = leng[1];
			this.plout.write(lvf);
			x = 9;
			y = 73;
			z = 9;
			yaw = 0;
			pitch = 0;
			byte initpos[] = new byte[10];
			byte[] pldat = new byte[8];
			initpos[0] = 8;
			initpos[1] = -1;
			pldat = data();
			for (byte t = 0; t<8; t++) {
				initpos[t+2] = pldat[t];
			}
			plout.write(initpos);
			level.pavv[id] = true;
			level.getplayers(id);
			level.introplayer(id);
			byte cycles = 99;
			byte[] plyin = new byte[85];
			byte[] plyin2 = new byte[85];
			byte[] plyin3 = new byte[75];
			byte[] tarr = new byte[2];
			short plx;
			short ply;
			short plz;
			byte blockid;
			short tx;
			short ty;
			short tz;
			byte tyaw;
			byte tpitch;
			boolean pl3;
			short[] posi = new short[5];
			String[] commands = new String[9];
			byte argnum;
			String names = new String(name);
			byte[] plex = new byte[]{0x0c, id};
			while (true) {
				for (byte clr = 0; clr<76; clr++) {
					plyin[clr] = 0;
				}
				this.plin.read(plyin);
				if (true) {
					pl3 = false;
					if (plyin[0] == 8) {
						tarr[0] = plyin[2];
						tarr[1] = plyin[3];
						tx = ByteBuffer.wrap(tarr).getShort();
						tarr[0] = plyin[4];
						tarr[1] = plyin[5];
						ty = ByteBuffer.wrap(tarr).getShort();
						tarr[0] = plyin[6];
						tarr[1] = plyin[7];
						tz = ByteBuffer.wrap(tarr).getShort();
						tyaw = plyin[8];
						tpitch = plyin[9];
						posi = pos();
						level.intromovement(id, (byte) (tx - posi[0]), (byte) (ty - posi[1]), (byte) (tz - posi[2]), (byte) posi[3], tyaw, (byte) posi[4], tpitch);
						if (((tx - posi[0]) != 0) | (ty - posi[1] != 0) | (tz - posi[2] != 0) | ((tyaw - posi[3]) != 0) | ((tpitch - posi[4]) != 0)) {
							store(tx, ty, tz, tyaw, tpitch);
						}
						for (byte ind = 0; ind<66; ind++) {
							plyin3[ind] = plyin[ind + 10];
						}
						pl3 = true;
					}
					if ((plyin3[0] == 5) & pl3) {
						tarr[0] = plyin3[1];
						tarr[1] = plyin3[2];
						plx = ByteBuffer.wrap(tarr).getShort();
						tarr[0] = plyin3[3];
						tarr[1] = plyin3[4];
						ply = ByteBuffer.wrap(tarr).getShort();
						tarr[0] = plyin3[5];
						tarr[1] = plyin3[6];
						plz = ByteBuffer.wrap(tarr).getShort();
						blockid = plyin3[8];
						if (plyin3[7] == 0) {
							blockid = 0;
						}
						level.lvl.place(blockid, plx, ply, plz, id);
						for (byte ind = 0; ind<66; ind++) {
							plyin3[ind] = plyin3[ind + 9];
						}
						for (byte ind = 0; ind<9; ind++) {
							plyin3[66+ind] = 0;
						}
					}
					if ((plyin3[0] == 5) & pl3) {
						tarr[0] = plyin3[1];
						tarr[1] = plyin3[2];
						plx = ByteBuffer.wrap(tarr).getShort();
						tarr[0] = plyin3[3];
						tarr[1] = plyin3[4];
						ply = ByteBuffer.wrap(tarr).getShort();
						tarr[0] = plyin3[5];
						tarr[1] = plyin3[6];
						plz = ByteBuffer.wrap(tarr).getShort();
						blockid = plyin3[8];
						if (plyin3[7] == 0) {
							blockid = 0;
						}
						level.lvl.place(blockid, plx, ply, plz, id);
					}
					if ((plyin3[0] == 0x0d) & pl3) {
						if (chatperms != -1) {
							if (plyin3[2] != 47) {
								level.broadcast((byte) -1, plyin3, id);
							} else {
								commands = parsetocommands(plyin3);
								argnum = Byte.valueOf(commands[0]);
								if (commands[1].equals("stop")) {
									System.out.println("Server stop initiated by player " + id + " (" + names +  "), program ending...");
									System.exit(0);
								}
							}
						}
					}
				}
				if (cycles == 99) {
					try {
						this.plout.write(parr);
					}
					catch (Exception exc){
						level.pavv[id] = true;
						for (byte t = 0; t<17; t++) {
							if (level.pavv[t]) {
								player.plist[t].plout.write(plex);
							}
						}
						level.sltak[id] = false;
						return;
					}
					cycles = -1;
				}
				cycles++;
				plyin2 = plyin;
		    }
		}
		catch (Exception exc) {
				System.out.println(exc);
				System.exit(0);
		}
	}
	public synchronized void store(short x, short y, short z, byte yaw, byte pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	public static char[] tofirstspace(byte[] tbs) {
		short tleng = 0;
		for (short t = 0; t<tbs.length; t++) {
			tleng = t;
			if (tbs[t] == 32) {
				break;
			}
		}
		char[] output = new char[tleng];
		for (short t = 0; t<tleng; t++) {
			output[t] = (char) tbs[t];
		}
		return output;
	}
	public static String[] parsetocommands(byte[] txt) {
		byte[] newt = new byte[65];
		for (byte t = 0; t<62; t++) {
			newt[t] = txt[t + 3];
		}
		newt[62] = 32;
		newt[63] = 32;
		newt[64] = 32;
		String[] outs = new String[9];
		outs[0] = "8";
		byte newtl;
		for (byte t = 1; t<9; t++) {
			char[] out = tofirstspace(newt);
			if (out.length == 0) {
				outs[0] = String.valueOf(t-1);
				break;
			}
			outs[t] = new String(out);
			newtl = (byte) newt.length;
			byte[] nnt = new byte[newtl - (out.length + 1)];
			for (byte u = 0; u < (newtl - (out.length + 1)); u++) {
				nnt[u] = newt[u + (out.length + 1)];
			}
			newt = nnt;
		}
		return outs;
	}
	public synchronized byte[] data() {
		byte[] ply = new byte[8];
		byte[] leng = new byte[2];
		leng = ByteBuffer.allocate(2).putShort(x).array();
		ply[0] = leng[0];
		ply[1] = leng[1];
		leng = ByteBuffer.allocate(2).putShort(y).array();
		ply[2] = leng[0];
		ply[3] = leng[1];
		leng = ByteBuffer.allocate(2).putShort(z).array();
		ply[4] = leng[0];
		ply[5] = leng[1];
		ply[6] = yaw;
		ply[7] = pitch;
		return ply;
	}
	public synchronized short[] pos() {
		short[] posi = new short[5];
		posi[0] = x;
		posi[1] = y;
		posi[2] = z;
		posi[3] = yaw;
		posi[4] = pitch;
		return posi;
	}
	public static void main(String[] args) {
		try {
			level.lvl = new level(1, 2, 3);
			level.lvl.genfloor((byte) 0x01);
			level.lvl.insert((byte) 2, 2, 2, 1);
			ServerSocket serverSocket = new ServerSocket(25566);
			System.out.println("Server started on port 25566...");
			byte[] condar = new byte[131];
			int conds;
			String servername = new String("testingServer                                                   ");
			String motd = new String("029d841198a9a400a949beda04eb460614bd46ec0dff01cdd21252d8f9b3c10c");
			byte[] serb = new byte[64];
			byte[] motb = new byte[64];
			player.plist[0] = new player((byte) 0x07, 5, null, (byte) 0);
			byte curpe = 1;
			level.curp = player.donothing();
			while(true) {
				for (byte t = 1; t < 17; t++) {
					curpe = t;
					if (level.sltak[t] == false) {
						break;
					}
					if (t == 16) {
						t = 1;
					}
				}
				level.sltak[curpe] = true;
				level.curp = curpe;
				player.plist[curpe] = new player((byte) 0x07, 5, serverSocket.accept(), curpe);
		        new Thread(new Runnable() {
		            public void run() {player.plist[0].play(); }
		        }).start();
		        while (level.curp == curpe) {
		        	Thread.sleep(500);
		        }
			}
		}
		catch(Exception exc){
			System.out.println("player error occured: " + exc);
		}
	    finally {}
		System.out.println("Program ending...");
		System.exit(0);
	}
}
