package coolassic;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
public class role {
	static Set<String> nl = new HashSet<String>();
	static ArrayList<role> rl = new ArrayList<role>();
	static NavigableSet<Integer> il = new TreeSet<Integer>();
	static Set<Integer> di = new HashSet<Integer>();
	static short tl;
	String name;
	short level;
	int id;
	public role(String name, short level, int id) {
		this.name = name;
		this.level = level;
		this.id = id;
	}
	public static synchronized boolean exists(String snm) {
		return nl.contains(snm);
	}
	public static synchronized boolean exists(int snm) {
		return il.contains(snm);
	}
	public static synchronized int[] make(String nm, short lvl, int ident) {
		boolean foundslot = false;
		int srch = 1;
		while (!foundslot && srch < 1000000000) {
			if (!(il.contains(srch) || di.contains(srch))) {
				foundslot = true;
				break;
			}
			srch++;
		}
		if (srch == 1000000000) {
			return new int[] {1, 0};
		}
		if (exists(nm)) {
			return new int[] {2, 0};
		}
		ident = srch;
		nl.add(nm);
		il.add(ident);
		rl.add(new role(nm, lvl, ident));
		return new int[] {0, ident};
	}
	public static synchronized boolean remove(String na) {
		if (!exists(na)) {
			return false;
		}
		role tr = getby(na);
		if (tr == null) {
			return false;
		}
		return remove(tr.id);
	}
	public static synchronized boolean remove(int ident) {
		if (!exists(ident)) {
			return false;
		}
		int loc = locate(ident);
		role rtr = rl.get(loc);
		String nam = rl.get(loc).name;
		rl.remove(loc);
		nl.remove(nam);
		il.remove(ident);
		di.add(ident);
		for (byte g = 0; g < (player.plamnt + 1); g++) {
			if (coolassic.level.pavv[g]) {
				if (player.plist[g].roles.contains(rtr)) {
					synchronized (player.plist[g].tobj) {
						if (player.plist[g].toplev == rtr.level && player.plist[g].toplev != 0) {
							tl = 0;
							player.plist[g].roles.forEach((ro) -> {
								if (ro.level > tl && !di.contains(ro.id)) {
									tl = ro.level;
								}
							});
							player.plist[g].toplev = tl;
						}
					}
				}
			}
		}
		return true;
	}
	public static synchronized int locate(int ident) {
		if (!exists(ident)) {
			return -1;
		}
	    return rl.indexOf(rl.stream().filter(roles -> ident == (roles.id)).findFirst().orElse(null));
	}
	public static synchronized int locate(String ident) {
		if (!exists(ident)) {
			return -1;
		}
	    return rl.indexOf(rl.stream().filter(roles -> ident.equals(roles.name)).findFirst().orElse(null));
	}
	public static synchronized String[] next(int num, Integer basev) {
		Integer ctr = basev;
		int lpts = 0;
		boolean exittype = false;
		String out = new String();
		out = "role list:";
		while (lpts <= num || num == 0) {
			lpts++;
			ctr = il.higher(ctr);
			if (ctr == null) {
				exittype = true;
				break;
			}
			Integer nei = ctr;
			role curr = rl.stream().filter(roles -> nei == (roles.id)).findFirst().orElse(null);
			out = out + "\n\nid: " + ctr + "\n" + "name: " + curr.name + "\n" + "level: " + curr.level;
		}
		if (exittype == false) {
			if (il.higher(ctr) == null) {
				exittype = true;
			}
		}
		if (lpts == 1) {
			out = "there are no roles";
		}
		String done = new String();
		done = "no";
		if (exittype) {
			done = "yes";
		}
		return new String[]{out, done};
	}
	public static synchronized String[] nextplayer(int num, Integer basev, byte plrid) {
		Integer ctr = basev;
		int lpts = 0;
		boolean exittype = false;
		String out = new String();
		out = "roles owned by " + player.plist[plrid].names + ":";
		while (lpts <= num || num == 0) {
			lpts++;
			ctr = player.plist[plrid].rlids.higher(ctr);
			if (ctr == null) {
				exittype = true;
				break;
			}
			while (di.contains(ctr)) {
				ctr = player.plist[plrid].rlids.higher(ctr);
				if (ctr == null) {
					exittype = true;
					break;
				}
			}
			if (exittype) {
				break;
			}
			Integer nei = ctr;
			role curr = rl.stream().filter(roles -> nei.equals(roles.id)).findFirst().orElse(null);
			out = out + "\n\nid: " + ctr + "\n" + "name: " + curr.name + "\n" + "level: " + curr.level;
		}
		if (exittype == false) {
			if (il.higher(ctr) == null) {
				exittype = true;
			}
		}
		if (lpts == 1) {
			out = "there are no roles";
		}
		String done = new String();
		done = "no";
		if (exittype) {
			done = "yes";
		}
		return new String[]{out, done};
	}
	public static synchronized role getby(int iden) {
		int space = locate(iden);
		if (space == -1) {
			return null;
		}
		return rl.get(space);
	}
	public static synchronized role getby(String name) {
		int space = locate(name);
		if (space == -1) {
			return null;
		}
		return rl.get(space);
	}
	public static synchronized byte giverole(byte plyid, String rolename, int roleid) {
		role newrole;
		if (rolename == null) {
			newrole = role.getby(roleid);
		}
		else {
			newrole = role.getby(rolename);
		}
		if (newrole == null) {
			return 1;
		}
		if (player.plist[plyid].roles.contains(newrole)) {
			return 2;
		}
		else {
			player.plist[plyid].rlids.add(newrole.id);
			player.plist[plyid].roles.add(newrole);
			synchronized (player.plist[plyid].tobj) {
				if (player.plist[plyid].toplev < newrole.level) {
					player.plist[plyid].toplev = newrole.level;
				}
			}
			return 0;
		}
	}	
	public static synchronized byte takerole(byte plyid, String rolename, int roleid) {
		role newrole;
		if (rolename == null) {
			newrole = role.getby(roleid);
		}
		else {
			newrole = role.getby(rolename);
		}
		if (newrole == null) {
			return 1;
		}
		if (player.plist[plyid].roles.contains(newrole)) {
			player.plist[plyid].rlids.remove(newrole.id);
			player.plist[plyid].roles.remove(newrole);
			synchronized (player.plist[plyid].tobj) {
				if (player.plist[plyid].toplev == newrole.level && player.plist[plyid].toplev != 0) {
					if (player.plist[plyid].roles.isEmpty()) {
						player.plist[plyid].toplev = 0;
					}
					else {
						tl = 0;
						player.plist[plyid].roles.forEach((ro) -> {
							if (ro.level > tl) {
								tl = ro.level;
							}
						});
						player.plist[plyid].toplev = tl;
					}
				}
			}
			return 0;
		}
		else {
			return 2;
		}
	}
	public static synchronized short loplvl(byte plyid) {
		return player.plist[plyid].toplev;
	}
	public static synchronized boolean hasrole(byte plyid, int rid) {
		if (player.plist[plyid].rlids.contains((Integer) rid) && !di.contains((Integer) rid)) {
			return true;
		}
		else {
			return false;
		}
	}
}
