package api.project.Game;

import javax.swing.JOptionPane;

public class Weapon {
	private int ammo;
	private int maxAmmo;

	public Weapon(int maxAmmo) {
		this.maxAmmo = maxAmmo;
		this.ammo = maxAmmo;
	}

	public int getAmmo() {
		return ammo;
	}

	public void reload() {
		ammo = maxAmmo;
	}

	public boolean shoot() {
		if (canShoot()) {
			ammo--;
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Out of ammo. Reload to shoot.");
			return false;
		}
	}
	public boolean canShoot() {
		return ammo > 0;
	}
}
