package de.raidcraft.loot.autoplacer;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * @author Philip
 */
public class LocationChecker {

    public final static LocationChecker INST = new LocationChecker();

    public void checkNextLocation(Location location) {

        /* check here if chest can be set */

        // check if other chests too close

        // check if wrong region



        // enqueue here things that can only be done by synchronous tasks
        AddInformationTask addInformationTask = new AddInformationTask(new CreatedChestInformation(location));
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RaidCraft.getComponent(LootPlugin.class), addInformationTask, 1, 10);
        addInformationTask.setTaskId(taskId);
    }

    public class AddInformationTask implements Runnable {

        private CreatedChestInformation createdChestInformation;
        private int taskId;

        public AddInformationTask(CreatedChestInformation createdChestInformation) {

            this.createdChestInformation = createdChestInformation;
        }

        public void setTaskId(int taskId) {

            this.taskId = taskId;
        }

        @Override
        public void run() {
            if((AutomaticPlacer.INST.addCreatedChestInformatin(createdChestInformation))) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
        }
    }
}
