package com.sweeneyb.infra


data class Cluster(var clusterName: String? = null, var namespaces: MutableList<String> = mutableListOf())
//data class Cluster(var )
data class Database(var id: String? = null, var username: String? = null, var password: String? = null)
data class Project(var name: String? = null,
                   var databases: MutableList<Database> = mutableListOf(),
                   var kubernetes: HashMap<String, Cluster> = HashMap(),
                    var iam: MutableList<IAM> = mutableListOf())
data class IAM(var account: String? = null, var roles: MutableList<String> = mutableListOf())


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
     cluster.apply ( block )
}

fun Cluster.namespaces( vararg toAdd: String) {
    namespaces.addAll(toAdd)
}

fun Database.named(name: String) {
    this.apply { id = name }
}

fun Database.users(name: String) {
    this.apply { username = name }
}

fun Project.kubernetes( block: Project.() -> Unit) {
    this.apply(block)
}

fun main(args: Array<String>) {
    val proj = project {
        
        withDatabase {
            named (" foo")
            users ( "user1 ")
        }
        withDatabase{
            named ( "bar ")
        }
        kubernetes {
           inCluster ("clusterName"){
                    namespaces("ns1", "ns2")
                }
            }

        }




    System.out.println(proj.databases.size)
    println(proj.kubernetes.get("clusterName"))
}