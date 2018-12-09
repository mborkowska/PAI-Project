package api.project.Game;

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
		if (ammo > 0) {
			ammo--;
			return true;
		} else {
			System.out.println("Out of ammo");
			return false;
		}
	}
}
