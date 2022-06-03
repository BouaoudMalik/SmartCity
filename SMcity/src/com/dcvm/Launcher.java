package com.dcvm;

import fr.sorbonne_u.components.cvm.utils.DCVM_Launcher;

/**
 * Classe qui permet de lancer la distributedDCVM avec le registre global et la
 * barrière cyclic
 *
 */
public class Launcher {

	public static void main(String[] args) {
		assert args != null && args.length >= 1;

		try {
			DCVM_Launcher launcher = new DCVM_Launcher("src/com/dcvm/config.xml");
			launcher.launch();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}