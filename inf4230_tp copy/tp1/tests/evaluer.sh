#!/bin/bash
##########################################################################
# UQAM - Département d'informatique
# INF4230 - Intelligence artificielle
# TP1 - Rétablir l'électricité, Algorithme A*
# http://ericbeaudry.ca/INF4230/tp1/
#
# Script d'évaluation du TP1
#
# Instructions:
# 1. Déposer ce script avec les fichiers problèmes dans un répertoire
#    distinct (ex: tests).
# 2. Se placer dans le répertoire contenant votre programme ou contenant
#    la liste des soumissions Oto (*.tp_oto).
# 3. Lancer ce script (ex: ../tests/evaluer.sh).
#
# Limites:
#  - Testé sous Ubuntu Linux
#  - Non testé sous mac OS (/usr/bin/time a un comportement différent)
#
##########################################################################


# Obtenir le chemin du répertoire contenant le présent script et les fichiers tests
pushd `dirname $0` >/dev/null
repertoire_tests=`pwd`
tests=`ls grille[0-9][0-9].txt`
popd >/dev/null

echo "#################################"
echo "UQAM | Département d'informatique"
echo "INF4230 | Intelligence artificielle"
echo "Évaluation du TP1"
echo "#################################"
echo

if [ `pwd` -ef $repertoire_tests ];
then
    echo "Ce script doit être dans un répertoire différent de celui contenant votre tp1."
    echo "Ce script a été arrêté afin de ne pas écraser les fichiers grille[0-9]+.txt !"
    exit -2;
fi

# Le simulateur doit être préalablement compilé.
simulateur="${repertoire_tests}/simre"

# Détection du CPU
if [ -e /proc/cpuinfo ] ; then
    cpuinfo=`grep "model name" /proc/cpuinfo | sort -u | cut -d ":" -f 2`
else
    cpuinfo="?"
fi

function Nettoyer
{
    echo "Nettoyage"
    # Au cas où le Makefile des étudiants ne ferait pas un «make clean» correctement.
    make clean

    rm -f *.o* *.gch tp[1-3] grille*+.txt
    # Au besoin, nettoyer les précédents fichiers logs
    rm -f log*.txt
}

function EvaluerTP
{
    logfile="log-`date +%Y%m%d_%H%M%S`.txt"
    echo "##############################################" > ${logfile}
    echo "#INF4230 Rapport de correction automatique   #" >> ${logfile}
    echo "##############################################" >> ${logfile}
    echo "Log : ${logfile}" >> ${logfile}
    codesMS=`find -regex ".*\.\(txt\|cpp\)" -exec cat {} \; | grep -E -o  [a-zA-Z]{4}[0-9]{8} | sort -u`
    echo "Code(s) MS : $codesMS" >> ${logfile}
    echo "Code(s) MS : $codesMS"
    echo -e "\n\n" >> ${logfile}

    echo -e "\nDétection ..."
    echo -e "\nDétection ..." >> ${logfile}

    ## Detection du langage de programmation
    ### JAVA
    if [ -e TP1.java ]; then
        echo -e "\nCompilation Java ..."
        echo -e "\nCompilation Java ..." >> ${logfile}
        javac TP1.java 2>&1 >> ${logfile}

        if [ ! -e TP1.class ]; then
            echo "ERREUR : le fichier TP1.class n'a pas été produit !"
            echo "  ERREUR : le fichier TP1.class n'a pas été produit !" >> ${logfile}
            return
        fi
        ulimit -S -t 60 -f 1024
        for test in $tests;
        do
            echo "Test TP1 $test ..."
            t1="`(/usr/bin/time -f "%U\t%Mk" java -Xmx16g TP1 ${repertoire_tests}/${test} > ${test%.txt}+m1.txt) 2>&1 | tail -n 1`"
            echo "Test TP1 -m2 $test ..."
            t2="`(/usr/bin/time -f "%U\t%Mk" java -Xmx16g TP1 -m2 ${repertoire_tests}/${test} > ${test%.txt}+m2.txt) 2>&1 | tail -n 1`"
            SimulerPlan
        done
        ulimit -S -t unlimited -f unlimited -v unlimited
    fi

    ### C++
    if [ -e Makefile ]; then
        echo -e "\nCompilation C++ ..."
        echo -e "\nCompilation C++ ..." >> ${logfile}
        make tp1 2>&1 >> ${logfile}

        if [ ! -x tp1 ]; then
            echo "ERREUR : le fichier tp1 n'a pas été produit !"
            echo "  ERREUR : le fichier tp1 n'a pas été produit !" >> ${logfile}
            return
        fi
        ulimit -S -t 60 -f 1024 -v 16777216
        for test in $tests;
        do
            echo "Test TP1 $test ..."
            t1="`(/usr/bin/time -f "%U\t%Mk" ./tp1 ${repertoire_tests}/${test} > ${test%.txt}+m1.txt) 2>&1 | tail -n 1`"
            echo "Test TP1 -m2 $test ..."
            t2="`(/usr/bin/time -f "%U\t%Mk" ./tp1 -m2 ${repertoire_tests}/${test} > ${test%.txt}+m2.txt) 2>&1 | tail -n 1`"
            SimulerPlan
        done
        ulimit -S -t unlimited -f unlimited -v unlimited
    fi

    ### Python
    if [ -e tp1.py ]; then
        echo "Python : à écrire!!"
        ulimit -S -t 60 -f 1024 -v 16777216
        for test in $tests;
        do
            echo "Test tp1.my $test ..."
            t1="`(/usr/bin/time -f "%U\t%Mk" ./tp1.py ${repertoire_tests}/${test} > ${test%.txt}+m1.txt) 2>&1 | tail -n 1`"
            echo "Test TP1 -m2 $test ..."
            t2="`(/usr/bin/time -f "%U\t%Mk" ./tp1.py -m2 ${repertoire_tests}/${test} > ${test%.txt}+m2.txt) 2>&1 | tail -n 1`"
            SimulerPlan
        done
        ulimit -S -t unlimited -f unlimited -v unlimited
    fi

}

function SimulerPlan(){
    if [ -x $simulateur ]; then
        echo "Simuler $test..."

        plan1=`tail -n 1 ${test%.txt}+m1.txt`
        if [ "$plan1" == "IMPOSSIBLE" ] ; then
        but1="IMPOSSIBLE"
        m1="+inf"
        else
        but1=`echo $plan1 | $simulateur ${repertoire_tests}/${test} | tail -n 1 | grep BUT`
        m1=`echo $plan1 | $simulateur ${repertoire_tests}/${test} | grep M1 | tail -n 1 | cut -f 2`
        fi

        plan2=`tail -n 1 ${test%.txt}+m2.txt`
        if [ "$plan2" == "IMPOSSIBLE" ] ; then
        but2="IMPOSSIBLE"
        m2="+inf"
        else
        but2=`echo $plan2 | $simulateur ${repertoire_tests}/${test} | tail -n 1 | grep BUT`
        m2=`echo $plan2 | $simulateur ${repertoire_tests}/${test} | grep M2 | tail -n 1 | cut -f 2`
        fi
    else
        but1="?"
        m1="?"
        but2="?"
        m2="?"
    fi
    echo -e "Résultat 1: $t1\t$but1\t$m1"
    echo -e "Résultat 2: $t2\t$but2\t$m2"
    sommaire="${sommaire}\t\t${t1}\t${but1}\t${m1}\t${t2}\t${but2}\t${m2}"
}


# Lister les soumissions Oto (répertoires terminant par .tp_oto)
tps=`ls -1 | grep .tp_oto`
# Si aucun répertoire .tp_oto n'existe, essayer le répertoire courant (auto-évaluation)
if [ ! -n "$tps" ]; then
    tps="."
fi

# Génération de l'entête du rapport
daterap=`date +%Y%m%d_%H%M%S`
rapport="rapport-${daterap}.txt"
echo "#Rapport de correction INF3105 / TP1" > $rapport
echo -e "#Date:\t${daterap}" >> $rapport
echo -e "#Machine :\t" `hostname` >> $rapport
echo -e "#CPU :\t$cpuinfo" >> $rapport
echo >> $rapport

# Génération des titres des colonnes
echo -n -e "\t\t" >> $rapport
for test in $tests;
do
   echo -n -e "\t$test\t\t\t\t\t\t\t\t" >> $rapport
done
echo >> $rapport

echo -n -e "Soumission\t" >> $rapport
for test in $tests;
do
    echo -n -e "\t\tCPU1\tMem1(k)\tBut1\tM1\tCPU2\tMem2(k)\tBut2\tM2" >> $rapport
done
echo >> $rapport


# Itération sur chaque TP
for tp in $tps; do
    sommaire=""
    echo "## Évaluation du répertoire : $tp"
    pushd $tp
    	EvaluerTP
#       Nettoyer
    popd
    echo -e ">> ${sommaire}"
    echo -e "${tp}\t${sommaire}" >> $rapport
done

