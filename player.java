package coolassic;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
public class player implements Runnable {
	public static byte plamnt = 64;
	char[] name = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	byte pvn;
	char[] verif = new char[64];
	String names;
	String verifs;
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
	byte strlock;
	String ip;
	boolean solid;
	boolean op;
	public static player[] plist = new player[plamnt + 1];
	public player(byte prot, int pf, Socket socke, byte ident) {
		pvn = prot;
		sock = socke;
		id = ident;
		chatperms = 0;
		solid = false;
		op = true;
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
		byte[] plex = new byte[]{0x0c, id};
		try {
			InetSocketAddress sad = (InetSocketAddress) sock.getRemoteSocketAddress();
			InetAddress iad = sad.getAddress();
			ip = iad.toString();
			System.out.println("New player joined, id:" + id + ", ip: " + ip);
			byte[] condar = new byte[131];
			String servername = new String("testingServer                                                   ");
			String motd = new String("029d841198a9a400a949beda04eb460614bd46ec0dff01cdd21252d8f9b3c10c");
			byte[] serb = new byte[64];
			byte[] motb = new byte[64];
			plin = this.sock.getInputStream();
			plout = new DataOutputStream(this.sock.getOutputStream());
			byte[] clie = new byte[131];
			this.plin.read(condar);
			if (condar[0] != 0) {
				level.pavv[id] = false;
				for (byte t = 0; t<(plamnt + 1); t++) {
					if (level.pavv[t]) {
						try {
							player.plist[t].plout.write(plex);
							level.msg(t, names + " left the game");
						}
						catch (Exception exce) {
							System.out.println(exce);
						}
					}
				}
				System.out.println("client handshake packet had an incorrect packet ID");
				level.sltak[id] = false;
				return;
			}
			System.out.println("Client is connecting with protocol version " + condar[1]);
			name = new char[64];
			verif = new char[64];
			for (byte n = 0; n<64; n++) {
				this.name[n] = (char) condar[n+2];
				this.verif[n] = (char) condar[n+66];
			}
			this.pvn = condar[1];
			byte pleng = 0;
			for (byte t = 63; t>=0; t--) {
				pleng = t;
				if (name[t] != ' ') {
					pleng = (byte) (t+1);
					break;
				}
			}
			char[] nac = new char[pleng];
			byte r = 0;
			while (r < pleng) {
				nac[r] = name[r];
				r++;
			}
			names = new String(nac);
			pleng = 0;
			for (byte t = 63; t>=0; t--) {
				pleng = t;
				if (verif[t] != ' ') {
					pleng = (byte) (t+1);
					break;
				}
			}
			nac = new char[pleng];
			r = 0;
			while (r < pleng) {
				nac[r] = verif[r];
				r++;
			}
			verifs = new String(nac);
			System.out.print("username is: ");
			System.out.print(names);
			System.out.println("");
			System.out.print("key is: ");
			System.out.println(verifs);
			serb = servername.getBytes(StandardCharsets.US_ASCII);
			motb = motd.getBytes(StandardCharsets.US_ASCII);
			clie[1] = 0x07;
			if (op) {
				clie[130] = 0x64;
			}
			else {
				clie[130] = 0;
			}
			for (byte n = 0; n<64; n++) {
				clie[n+2] = serb[n];
				clie[n+66] = motb[n];
			}
			this.plout.write(clie);
			System.out.println("");
			System.out.println("Sent server name and MOTD to client");
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
			x = 10;
			y = 73;
			z = 10;
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
			byte tb;
			byte sb;
			byte hb;
			short ts;
			short ss;
			short hs;
			double td;
			boolean dataerr = false;
			byte[] telp = new byte[10];
			byte qb;
			boolean drd;
			while (true) {
				for (byte clr = 0; clr<76; clr++) {
					plyin[clr] = 0;
				}
				plin.read(plyin);
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
					level.intromovement(id, (tx - posi[0]), (ty - posi[1]), (tz - posi[2]), (byte) posi[3], tyaw, (byte) posi[4], tpitch, tx, ty, tz);
					if (((tx - posi[0]) != 0) | (ty - posi[1] != 0) | (tz - posi[2] != 0) | ((tyaw - posi[3]) != 0) | ((tpitch - posi[4]) != 0)) {
						store(tx, ty, tz, tyaw, tpitch);
					}
					for (byte ind = 0; ind<66; ind++) {
						plyin3[ind] = plyin[ind + 10];
					}
					pl3 = true;
				}
				if ((plyin3[0] == 5) && pl3) {
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
				if ((plyin3[0] == 5) && pl3) {
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
				if ((plyin3[0] == 0x0d) && pl3) {
					if (chatperms != -1) {
						if (plyin3[2] != 47) {
							level.broadcast((byte) -1, plyin3, id);
						} else {
							commands = parsetocommands(plyin3);
							argnum = Byte.valueOf(commands[0]);
							commands[1] = commands[1].toLowerCase();
							dataerr = false;
							switch (commands[1]) {
							case ("tpa"):
								if (!solid((byte) 0)[1]) {
									level.msg(id, "you do not have access to this command");
									break;
								}
								ts = 0;
								ss = 0;
								hs = 0;
								if (argnum < 3) {
									level.msg(id, "invalid command syntax for /tpa");
									break;
								}
								if (commands[2].equals("@s")) {
									hb = id;
								}
								else if (commands[2].equals("@a")) {
									hb = -1;
								}
								else {
									hb = resolveplayername(commands[2]);
									if (hb == -1) {
										level.msg(id, commands[2] + " is not connected");
										break;
									}
								}
								if (commands[3].equals("@s")) {
									qb = id;
								}
								else {
									qb = resolveplayername(commands[3]);
									if (qb == -1) {
										level.msg(id, commands[3] + " is not connected");
										break;
									}
								}
								ts = plist[qb].pos()[0];
								ss = plist[qb].pos()[1];
								hs = plist[qb].pos()[2];
								leng = ByteBuffer.allocate(2).putShort(ts).array();
								telp[2] = leng[0];
								telp[3] = leng[1];
								leng = ByteBuffer.allocate(2).putShort(ss).array();
								telp[4] = leng[0];
								telp[5] = leng[1];
								leng = ByteBuffer.allocate(2).putShort(hs).array();
								telp[6] = leng[0];
								telp[7] = leng[1];
								telp[8] = (byte) plist[qb].pos()[3];
								telp[9] = (byte) plist[qb].pos()[4];
								telp[1] = -1;
								telp[0] = 8;
								if (hb != -1) {
									plist[hb].store(ts, ss, hs, telp[8], telp[9]);
									plist[hb].plout.write(telp);
								}
								else {
									for (byte t = 0; t<(player.plamnt + 1); t++) {
										if (level.pavv[t]) {
											plist[t].store(ts, ss, hs, telp[8], telp[9]);
											player.plist[t].plout.write(telp);
										}
									}
								}
								if (hb != -1) {
									telp[1] = hb;
									for (byte i = 0; i < (plamnt + 1); i++) {
										if ((level.pavv[i]) && (i != hb)) {
											plist[i].plout.write(telp);
										}
									}
								}
								else {
									for (byte u = 0; u < (plamnt + 1); u++) {
										if (level.pavv[u]) {
											telp[1] = u;
											for (byte i = 0; i < (plamnt + 1); i++) {
												if ((level.pavv[i]) && (i != u)) {
													plist[i].plout.write(telp);
												}
											}
										}
									}
								}
								break;
							case ("tp"):
								if (!solid((byte) 0)[1]) {
									level.msg(id, "you do not have access to this command");
									break;
								}
								ts = 0;
								ss = 0;
								hs = 0;
								if (argnum < 5) {
									level.msg(id, "invalid command syntax for /tp");
									break;
								}
								dataerr = false;
								if (commands[2].equals("@s")) {
									hb = id;
								}
								else if (commands[2].equals("@a")) {
									hb = -1;
								}
								else {
									hb = resolveplayername(commands[2]);
									if (hb == -1) {
										level.msg(id, commands[2] + " is not connected");
										break;
									}
								}
								if (!commands[3].equals("~") || hb == -1) {
									try {
										td = Double.valueOf(commands[3]);
									}
									catch(Exception exc) {
										level.msg(id, commands[3] + " is not a valid double value");
										break;
									}
									ts = (short) ((td * 32) + 16);
								}
								if (!commands[4].equals("~") || hb == -1) {
									try {
										td = Double.valueOf(commands[4]);
									}
									catch(Exception exc) {
										level.msg(id, commands[4] + " is not a valid double value");
										break;
									}
									ss = (short) ((td * 32) + 51);
								}
								if (!commands[5].equals("~") || hb == -1) {
									try {
										td = Double.valueOf(commands[5]);
									}
									catch(Exception exc) {
										level.msg(id, commands[5] + " is not a valid double value");
										break;
									}
									hs = (short) ((td * 32) + 16);
									if (ss < 51) {
										ss = 51;
									}
									if (ts < 10) {
										ts = 10;
									}
									if (ts > ((level.lvl.dims[0] * 32) - 10)) {
										ts = (short) ((level.lvl.dims[0] * 32) - 10);
									}
									if (hs < 10) {
										hs = 10;
									}
									if (hs > ((level.lvl.dims[2] * 32) - 10)) {
										hs = (short) ((level.lvl.dims[2] * 32) - 10);
									}
								}
								tb = 0;
								sb = 0;
								if (argnum > 5) {
									if (!commands[6].equals("~") || hb == -1) {
										try {
											tb = Byte.valueOf(commands[6]);
										}
										catch(Exception exc) {
											level.msg(id, commands[6] + " is not a valid sByte value");
											break;
										}
									}
									if (argnum > 6) {
										if (!commands[7].equals("~") || hb == -1) {
											try {
												sb = Byte.valueOf(commands[7]);
											}
											catch(Exception exc) {
												level.msg(id, commands[7] + " is not a valid sByte value");
												break;
											}
										}
									}
								}
								if ((sb > 64) && (sb < -64)) {
									level.msg(id, commands[7] + " is out of the -64 to 64 domain, invalid yaw");
									break;
								}
								if (hb != -1) {
									if (commands[3].equals("~")) {
										ts = plist[hb].pos()[0];
									}
									if (commands[4].equals("~")) {
										ss = plist[hb].pos()[1];
									}
									if (commands[5].equals("~")) {
										hs = plist[hb].pos()[2];
									}
									if (argnum > 5) {
										if (commands[6].equals("~")) {
											tb = (byte) plist[hb].pos()[3];
										}
										if (argnum > 6) {
											if (commands[7].equals("~")) {
												sb = (byte) plist[hb].pos()[4];
											}
										}
									}
								}
								leng = ByteBuffer.allocate(2).putShort(ts).array();
								telp[2] = leng[0];
								telp[3] = leng[1];
								leng = ByteBuffer.allocate(2).putShort(ss).array();
								telp[4] = leng[0];
								telp[5] = leng[1];
								leng = ByteBuffer.allocate(2).putShort(hs).array();
								telp[6] = leng[0];
								telp[7] = leng[1];
								telp[8] = tb;
								telp[9] = sb;
								telp[0] = 8;
								telp[1] = -1;
								if (hb != -1) {
									plist[hb].store(ts, ss, hs, tb, sb);
									System.out.println(telp[8]);
									plist[hb].plout.write(telp);
								}
								else {
									for (byte t = 0; t<(player.plamnt + 1); t++) {
										if (level.pavv[t]) {
											plist[t].store(ts, ss, hs, tb, sb);
											player.plist[t].plout.write(telp);
										}
									}
								}
								if (hb != -1) {
									telp[1] = hb;
									for (byte i = 0; i < (plamnt + 1); i++) {
										if ((level.pavv[i]) && (i != hb)) {
											plist[i].plout.write(telp);
										}
									}
								}
								else {
									for (byte u = 0; u < (plamnt + 1); u++) {
										if (level.pavv[u]) {
											telp[1] = u;
											for (byte i = 0; i < (plamnt + 1); i++) {
												if ((level.pavv[i]) && (i != u)) {
													plist[i].plout.write(telp);
												}
											}
										}
									}
								}
								break;
							case ("stop"):
								if (!solid((byte) 0)[1]) {
									level.msg(id, "you do not have access to this command");
									break;
								}
								System.out.println("Server stop initiated by player " + id + " (" + names +  "), program ending...");
								System.exit(0);
								break;
							case ("resolvename"):
								if (!solid((byte) 0)[1]) {
									level.msg(id, "you do not have access to this command");
									break;
								}
								if (argnum < 2) {
									level.msg(id, "invalid command syntax for /resolvename");
									break;
								}
								tb = resolveplayername(commands[2]);
								if (tb == -1) {
									level.msg(id, commands[2] + " is not connected");
								}
								else {
									level.msg(id, "slot " + tb + " contains " + commands[2]);
								}
								break;
							case ("resolveid"):
								if (!solid((byte) 0)[1]) {
									level.msg(id, "you do not have access to this command");
									break;
								}
								if (argnum < 2) {
									level.msg(id, "invalid command syntax for /resolveid");
									break;
								}
								sb = 0;
								dataerr = false;
								try {
									sb = Byte.valueOf(commands[2]);
								}
								catch(Exception exc) {
									level.msg(id, commands[2] + " is not a valid sByte value");
									dataerr = true;
								}
								if (!dataerr) {
									if ((sb > 0) && (sb <= plamnt)) {
										if (level.pavv[sb]) {
											level.msg(id, "slot " + sb + " contains " + plist[sb].names);
										}
										else {
											level.msg(id, "no player occupies slot " + sb);
										}
									}
									else {
										level.msg(id, "invalid slot id " + sb + " for player amount " + plamnt);
									}
								}
								break;
							case ("getpos"):
								if (!solid((byte) 0)[1]) {
									level.msg(id, "you do not have access to this command");
									break;
								}
								if (argnum < 2) {
									level.msg(id, "invalid command syntax for /getpos");
									break;
								}
								tb = 0;
								if (commands[2].equals("@s")) {
									tb = id;
								}
								else {
									tb = resolveplayername(commands[2]);
								}
								if (tb == -1) {
									level.msg(id, commands[2] + " is not connected");
									break;
								}
								dataerr = false;
								if (argnum > 2) {
									if (commands[3].equals("announce")) {
										dataerr = true;
									}
								}
								level.msg((byte) ((dataerr ? -1 : 0) + (dataerr ? 0 : 1) * id), plist[tb].names + " is located at " + ((plist[tb].pos()[0] - 16) / (double) 32) + ", " + ((plist[tb].pos()[1] - 51) / (double) 32) + ", " + ((plist[tb].pos()[2] - 16) / (double) 32) + ", " + plist[tb].pos()[3] + ", " + plist[tb].pos()[4]);
								break;
							case ("help"):
								level.msg(id, "Command List:\n"
										+ "/tpa <name>|@s|@a <name>|@s\n"
										+ "/tp <name>|@s|@a <x>|~ <y>|~ <z>|~ [<yaw>|~ [<pitch>|~]]\n"
										+ "/stop\n"
										+ "/resolvename <name>\n"
										+ "/resolveid <id>\n"
										+ "/getpos <name>|@s [announce]\n"
										+ "/solid [<name>]\n"
										+ "/op <name>\n"
										+ "/deop <name>\n");
								break;
							case ("solid"):
								if (!solid((byte) 0)[1]) {
									level.msg(id, "you do not have access to this command");
									break;
								}
								tb = id;
								if (argnum > 1) {
									if (commands[2].equals("@s")) {
										tb = id;
									}
									else {
										tb = resolveplayername(commands[2]);
									}
									if (tb == -1) {
										level.msg(id, commands[2] + " is not connected");
									}
								}
								dataerr = plist[tb].solid((byte) 1)[0];
								if (dataerr) {
									level.msg(id, plist[tb].names + " can now place unbreakable stone while op");
									if (tb != id) {
										level.msg(tb, "you can now place unbreakable stone when op");
									}
								}
								else {
									level.msg(id, plist[tb].names + " now cannot place unbreakable stone");
									if (tb != id) {
										level.msg(tb, "you now cannot place unbreakable stone");
									}
								}
								break;
							case ("deop"):
								dataerr = true;
							case ("op"):
								if (!solid((byte) 0)[1]) {
									level.msg(id, "you do not have access to this command");
									break;
								}
								if (argnum < 2) {
									if (dataerr) {
										level.msg(id, "invalid command syntax for /deop");
									}
									else {
										level.msg(id, "invalid command syntax for /op");
									}
									break;
								}
								tb = resolveplayername(commands[2]);
								if (tb == -1) {
									level.msg(id, commands[2] + " is not connected");
									break;
								}
								else if (tb == id) {
									if (dataerr) {
										level.msg(id, "you cannot deop yourself");
									}
									else {
										level.msg(id, "you are already an op");
									}
									break;
								}
								drd = plist[tb].solid((byte) 0)[1];
								if (!(drd ^ !dataerr)) {
									if (dataerr) {
										level.msg(id, commands[2] + " is already non-op");
									}
									else {
										level.msg(id, commands[2] + " is already an op");
									}
									break;
								}
								drd = plist[tb].solid((byte) (dataerr ? 3 : 2))[1];
								if (dataerr) {
									level.msg(id, commands[2] + " has been de-opped");
									level.msg(tb, "you have been de-opped");
								}
								else {
									level.msg(id, commands[2] + " has been opped");
									level.msg(tb, "you have been opped");
								}
								break;
							default:
								level.msg(id, "unknown command, use /help to view available commands and their syntaxes");
								break;
							}
						}
					}
				}
				if (cycles == 99) {
					this.plout.write(parr);
					cycles = -1;
				}
				cycles++;
			}
		}
		catch (Exception exc) {
				System.out.println(exc);
				level.pavv[id] = false;
				for (byte t = 0; t<(plamnt + 1); t++) {
					if (level.pavv[t]) {
						try {
							player.plist[t].plout.write(plex);
							level.msg(t, names + " left the game");
						}
						catch (Exception exce) {
							System.out.println(exce);
						}
					}
				}
				System.out.println(names + " left the game");
				level.sltak[id] = false;
				return;
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
	public static byte resolveplayername(String plnm) {
		for (byte t = 0; t<(player.plamnt + 1); t++) {
			if (level.pavv[t]) {
				if (plist[t].names.equals(plnm)) {
					return t;
				}
			}
		}
		return -1;
	}
	public boolean[] solid(byte change) {
		boolean[] solids = new boolean[2];
		byte[] opba = new byte[2];
		opba[0] = 0x0f;
		if (change == 1) {
			solid = !solid;
		}
		if (change < 4 && change > 1) {
			if (change == 2) {
				op = true;
				opba[1] = 0x64;
			}
			if (change == 3) {
				op = false;
				opba[1] = 0;
			}
			try {
				plout.write(opba);
			}
			catch (Exception exc) {
				System.out.println("player error - error sending user permissions packet: " + exc);
			}
		}
		solids[0] = solid;
		solids[1] = op;
		return solids;
	}
	public static void main(String[] args) {
		try {
			ServerSocket servsock = new ServerSocket(25566);
			level.lvl = new level(1, 2, 3);
			level.lvl.genfloor((byte) 0x01);
			level.lvl.insert((byte) 2, 2, 2, 1);
			System.out.println("Server started on port 25566...");
			player.plist[0] = new player((byte) 0x07, 5, null, (byte) 0);
			byte curpe = 1;
			level.curp = player.donothing();
			while(true) {
				for (byte t = 1; t < (plamnt + 1); t++) {
					curpe = t;
					if (level.sltak[t] == false) {
						break;
					}
					if (t == plamnt) {
						t = 1;
					}
				}
				level.sltak[curpe] = true;
				level.curp = curpe;
				player.plist[curpe] = new player((byte) 0x07, 5, servsock.accept(), curpe);
		        new Thread(new Runnable() {public void run() {player.plist[0].play();}}).start();
		        while (level.curp == curpe) {
		        	Thread.sleep(100);
		        }
			}
		}
		catch(Exception exc){
			System.out.println("player error occured: " + exc);
		}
		finally {
			System.out.println("Program ending...");
			System.exit(0);
	    }
	}
}
