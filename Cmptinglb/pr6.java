package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * A simple example showing how to create
 * two datacenters with one host each and
 * run two cloudlets on them.
 */
public class CloudSimExample4 {

    /** The cloudlet list. */
    private static List<Cloudlet> cloudletList;

    /** The VM list. */
    private static List<Vm> vmlist;

    /**
     * Creates main() to run this example
     */
    public static void main(String[] args) {

        Log.printLine("Starting CloudSimExample4...");

        try {

            // First step: Initialize the CloudSim package
            int num_user = 1; // number of cloud users

            Calendar calendar = Calendar.getInstance();

            boolean trace_flag = false; // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            @SuppressWarnings("unused")
            Datacenter datacenter0 =
                    createDatacenter("Datacenter_0");

            @SuppressWarnings("unused")
            Datacenter datacenter1 =
                    createDatacenter("Datacenter_1");

            // Third step: Create Broker
            DatacenterBroker broker = createBroker();

            int brokerId = broker.getId();

            // Fourth step: Create virtual machines
            vmlist = new ArrayList<Vm>();

            // VM description
            int vmid = 0;

            int mips = 250;

            long size = 10000; // image size (MB)

            int ram = 512; // vm memory (MB)

            long bw = 1000;

            int pesNumber = 1; // number of cpus

            String vmm = "Xen"; // VMM name

            // Create two VMs
            Vm vm1 = new Vm(
                    vmid,
                    brokerId,
                    mips,
                    pesNumber,
                    ram,
                    bw,
                    size,
                    vmm,
                    new CloudletSchedulerTimeShared()
            );

            vmid++;

            Vm vm2 = new Vm(
                    vmid,
                    brokerId,
                    mips,
                    pesNumber,
                    ram,
                    bw,
                    size,
                    vmm,
                    new CloudletSchedulerTimeShared()
            );

            // Add the VMs to the vmList
            vmlist.add(vm1);
            vmlist.add(vm2);

            // Submit VM list to the broker
            broker.submitVmList(vmlist);

            // Fifth step: Create two Cloudlets
            cloudletList = new ArrayList<Cloudlet>();

            // Cloudlet properties
            int id = 0;

            long length = 40000;

            long fileSize = 300;

            long outputSize = 300;

            UtilizationModel utilizationModel =
                    new UtilizationModelFull();

            Cloudlet cloudlet1 = new Cloudlet(
                    id,
                    length,
                    pesNumber,
                    fileSize,
                    outputSize,
                    utilizationModel,
                    utilizationModel,
                    utilizationModel
            );

            cloudlet1.setUserId(brokerId);

            id++;

            Cloudlet cloudlet2 = new Cloudlet(
                    id,
                    length,
                    pesNumber,
                    fileSize,
                    outputSize,
                    utilizationModel,
                    utilizationModel,
                    utilizationModel
            );

            cloudlet2.setUserId(brokerId);

            // Add cloudlets to the list
            cloudletList.add(cloudlet1);
            cloudletList.add(cloudlet2);

            // Submit cloudlet list to the broker
            broker.submitCloudletList(cloudletList);

            // Bind cloudlets to VMs
            broker.bindCloudletToVm(
                    cloudlet1.getCloudletId(),
                    vm1.getId()
            );

            broker.bindCloudletToVm(
                    cloudlet2.getCloudletId(),
                    vm2.getId()
            );

            // Sixth step: Start simulation
            CloudSim.startSimulation();

            // Final step: Print results
            List<Cloudlet> newList =
                    broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            printCloudletList(newList);

            Log.printLine("CloudSimExample4 finished!");

        } catch (Exception e) {

            e.printStackTrace();

            Log.printLine(
                    "The simulation has been terminated due to an unexpected error"
            );
        }
    }

    /**
     * Creates the datacenter.
     */
    private static Datacenter createDatacenter(String name) {

        // 1. Create a list to store machines
        List<Host> hostList = new ArrayList<Host>();

        // 2. Create a list of PEs
        List<Pe> peList = new ArrayList<Pe>();

        int mips = 1000;

        // 3. Create PEs and add them into a list
        peList.add(
                new Pe(
                        0,
                        new PeProvisionerSimple(mips)
                )
        );

        // 4. Create Host
        int hostId = 0;

        int ram = 2048; // host memory (MB)

        long storage = 1000000; // host storage

        int bw = 10000;

        // SpaceShared scheduling:
        // only one VM can run on each PE
        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram),
                        new BwProvisionerSimple(bw),
                        storage,
                        peList,
                        new VmSchedulerSpaceShared(peList)
                )
        );

        // 5. Create DatacenterCharacteristics object
        String arch = "x86"; // system architecture

        String os = "Linux"; // operating system

        String vmm = "Xen";

        double time_zone = 10.0;

        double cost = 3.0;

        double costPerMem = 0.05;

        double costPerStorage = 0.001;

        double costPerBw = 0.0;

        LinkedList<Storage> storageList =
                new LinkedList<Storage>();

        DatacenterCharacteristics characteristics =
                new DatacenterCharacteristics(
                        arch,
                        os,
                        vmm,
                        hostList,
                        time_zone,
                        cost,
                        costPerMem,
                        costPerStorage,
                        costPerBw
                );

        // 6. Create Datacenter object
        Datacenter datacenter = null;

        try {

            datacenter = new Datacenter(
                    name,
                    characteristics,
                    new VmAllocationPolicySimple(hostList),
                    storageList,
                    0
            );

        } catch (Exception e) {

            e.printStackTrace();
        }

        return datacenter;
    }

    /**
     * Creates the broker.
     */
    private static DatacenterBroker createBroker() {

        DatacenterBroker broker = null;

        try {

            broker = new DatacenterBroker("Broker");

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }

        return broker;
    }

    /**
     * Prints the Cloudlet objects.
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(
            List<Cloudlet> list
    ) {

        int size = list.size();

        Cloudlet cloudlet;

        String indent = " ";

        Log.printLine();

        Log.printLine("========== OUTPUT ==========");

        Log.printLine(
                "Cloudlet ID" + indent +
                "STATUS" + indent +
                "Data center ID" + indent +
                "VM ID" + indent +
                "Time" + indent +
                "Start Time" + indent +
                "Finish Time"
        );

        DecimalFormat dft =
                new DecimalFormat("###.##");

        for (int i = 0; i < size; i++) {

            cloudlet = list.get(i);

            Log.print(
                    indent +
                    cloudlet.getCloudletId() +
                    indent +
                    indent
            );

            if (cloudlet.getCloudletStatus()
                    == Cloudlet.SUCCESS) {

                Log.print("SUCCESS");

                Log.printLine(
                        indent + indent +
                        cloudlet.getResourceId() +
                        indent + indent + indent +
                        cloudlet.getVmId() +
                        indent + indent +
                        dft.format(
                                cloudlet.getActualCPUTime()
                        ) +
                        indent + indent +
                        dft.format(
                                cloudlet.getExecStartTime()
                        ) +
                        indent + indent +
                        dft.format(
                                cloudlet.getFinishTime()
                        )
                );
            }
        }
    }
}