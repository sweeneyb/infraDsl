package com.sweeney.infra


data class Cluster(var clusterName: String? = null, var namespaces: MutableList<String> = mutableListOf())
//data class Cluster(var )
data class Database(var id: String? = null, var username: String? = null, var password: String? = null)
data class Project(var name: String? = null, var databases: MutableList<Database> = mutableListOf(), var kubernetes: HashMap<String, Cluster> = HashMap())

fun project(block: Project.() -> Unit): Project {
    val p = Project()
    p.block()
    return p
}

fun Project.databases(block: MutableList<Database>.() -> Unit) {
    databases.apply(block)
}

fun Project.withDatabase( block: Database.() -> Unit) {
    val db = Database()
    db.block()
    databases.add(db)
}

fun Project.inCluster ( clusterName: String, block: Cluster.() -> Unit) {
    var cluster = kubernetes.get(clusterName)
    if( cluster == null) {
        cluster = Cluster()
        kubernetes.put(clusterName, cluster)
    }
     cluster.apply { block }
}

fun Cluster.namespaces( vararg toAdd: String) {
    namespaces.addAll(toAdd)
}

fun Database.named(name: String): Database {
    return Database(name)
}

fun main(args: Array<String>) {
    val proj = project {
        withDatabase {
            named (" foo")
        }
        withDatabase{
            named ( "bar ")
        }
        inCluster ("clusterName"){
                namespaces("ns1", "ns2")
            }
        }



    System.out.println(proj.databases.size)
}