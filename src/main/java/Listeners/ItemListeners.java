package Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import Main.Main;
import PartyCracker.Cracker;
import PartyCracker.Cracker.Particle;
import PartyCracker.Cracker.Sound;
import PartyCracker.PartyCrackerManager;

public class ItemListeners implements Listener{

	private Main main;
	public ItemListeners(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		Cracker cracker = convertItemToCracker(item.getItemStack());
		if(cracker == null) return;
		
		ItemStack reward = cracker.getReward();
		
		int delay = cracker.getExplodeDelay();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
		    @Override
		    public void run() {
		        explodeCracker(cracker, event.getItemDrop().getLocation());
		        item.getWorld().dropItem(item.getLocation(), reward);
		        item.remove();
		    }
		}, 20L*delay);
	}
	
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		Item item = event.getItem();
		if(convertItemToCracker(item.getItemStack()) == null) return;
		
		event.setCancelled(true);
	}
	
	public Cracker convertItemToCracker(ItemStack item) {
		PartyCrackerManager manager = main.getCrackerManager();
		for(Cracker cracker : manager.getPossibleCrackers()) {
			if(item.getItemMeta().equals(cracker.getItemMeta())) {
				return new Cracker(main, cracker.getName());
			}
		}
		return null;
	}
	
	private void explodeCracker(Cracker cracker, Location location) {
		Sound sound = cracker.getSound();
		
		createParticleExplosion(cracker, location);
		location.getWorld().playSound(location, sound.getSound(), sound.getVolume(), sound.getPitch());
	}
	
	private void createParticleExplosion(Cracker cracker, Location location) {
		Particle particle = cracker.getParticle();
		org.bukkit.Particle particles = particle.getParticle();
		DustOptions color = new DustOptions(particle.getColor(), 1);
		
		int amount = particle.getAmount();
		double offset = particle.getOffset(), velocity = particle.getVelocity();
		
		final double r = 0.2;
		double AngleTheta = 10;

		for (double theta = 0; theta < 180; theta += AngleTheta) {
			final double dphi = AngleTheta / Math.sin(Math.toRadians(theta));
			
			for (double phi = 0; phi < 360; phi += dphi) {
				final double rphi = Math.toRadians(phi);
				final double rtheta = Math.toRadians(theta);

				double x, y, z;
				x = r * Math.cos(rphi) * Math.sin(rtheta);
				y = r * Math.sin(rphi) * Math.sin(rtheta);
				z = r * Math.cos(rtheta);
				
				Location loc = new Location(location.getWorld(), x, y+0.2, z);

				if(particle.getParticle().equals(org.bukkit.Particle.REDSTONE)) location.getWorld().spawnParticle(particles, loc.add(location), amount, offset, offset, offset, velocity, color);
				else location.getWorld().spawnParticle(particles, loc.add(location), amount, offset, offset, offset, velocity);
			}
			
		}
	}
}
