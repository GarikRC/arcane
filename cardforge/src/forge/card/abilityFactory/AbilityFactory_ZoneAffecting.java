package forge.card.abilityFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import forge.AllZone;
import forge.AllZoneUtil;
import forge.Card;
import forge.CardList;
import forge.CardListUtil;
import forge.CardUtil;
import forge.ComputerUtil;
import forge.Constant;
import forge.Counters;
import forge.GameActionUtil;
import forge.MyRandom;
import forge.Player;
import forge.card.cardFactory.CardFactoryUtil;
import forge.card.spellability.Ability_Activated;
import forge.card.spellability.Ability_Sub;
import forge.card.spellability.Cost;
import forge.card.spellability.Spell;
import forge.card.spellability.SpellAbility;
import forge.card.spellability.Target;
import forge.gui.GuiUtils;

public class AbilityFactory_ZoneAffecting {
	
	//**********************************************************************
	//******************************* DRAW *********************************
	//**********************************************************************
	public static SpellAbility createAbilityDraw(final AbilityFactory AF){
		final SpellAbility abDraw = new Ability_Activated(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()){
			private static final long serialVersionUID = 5445572699000471299L;
			
			final AbilityFactory af = AF;
			
			@Override
			public String getStackDescription(){
			// when getStackDesc is called, just build exactly what is happening
				return drawStackDescription(af, this);
			}

			public boolean canPlayAI(){
				return drawCanPlayAI(af,this);
			}
			
			@Override
			public void resolve() {
				drawResolve(af, this);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return drawTrigger(af, this, mandatory);
			}
			
		};
		return abDraw;
	}
	
	public static SpellAbility createSpellDraw(final AbilityFactory AF){
		final SpellAbility spDraw = new Spell(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()){
			private static final long serialVersionUID = -4990932993654533449L;
			
			final AbilityFactory af = AF;
			
			@Override
			public String getStackDescription(){
				// when getStackDesc is called, just build exactly what is happening
				return drawStackDescription(af, this);
			}

			public boolean canPlayAI(){
				return drawCanPlayAI(af, this);
			}
			
			@Override
			public void resolve() {
				drawResolve(af, this);
			}
			
		};
		return spDraw;
	}
	
	public static SpellAbility createDrawbackDraw(final AbilityFactory AF){
		final SpellAbility dbDraw = new Ability_Sub(AF.getHostCard(), AF.getAbTgt()){
			private static final long serialVersionUID = -4990932993654533449L;
			
			final AbilityFactory af = AF;
			
			@Override
			public String getStackDescription(){
				// when getStackDesc is called, just build exactly what is happening
				return drawStackDescription(af, this);
			}

			@Override
			public void resolve() {
				drawResolve(af, this);
			}

			@Override
			public boolean chkAI_Drawback() {
				return drawTargetAI(af, this, false, false);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return drawTrigger(af, this, mandatory);
			}
			
		};
		return dbDraw;
	}
	
	public static String drawStackDescription(AbilityFactory af, SpellAbility sa){
		StringBuilder sb = new StringBuilder();
		
		if (!(sa instanceof Ability_Sub))
			sb.append(sa.getSourceCard().getName()).append(" - ");
		else
			sb.append(" ");
		
		String conditionDesc = af.getMapParams().get("ConditionDescription");
		if (conditionDesc != null)
			sb.append(conditionDesc).append(" ");
		
		ArrayList<Player> tgtPlayers;

		Target tgt = af.getAbTgt();
		if (tgt != null)
			tgtPlayers = tgt.getTargetPlayers();
		else
			tgtPlayers = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), af.getMapParams().get("Defined"), sa);
		
		if (tgtPlayers.size() > 0){
			for(Player p : tgtPlayers)
				sb.append(p.toString()).append(" ");
	
	        int numCards = 1;
	        if (af.getMapParams().containsKey("NumCards"))
	        	numCards = AbilityFactory.calculateAmount(sa.getSourceCard(), af.getMapParams().get("NumCards"), sa);
			
			sb.append("draws (").append(numCards).append(")");
			
			if (af.getMapParams().containsKey("NextUpkeep"))
				sb.append(" at the beginning of the next upkeep");
			
			sb.append(".");
		}
		
		Ability_Sub abSub = sa.getSubAbility();
        if (abSub != null){
        	sb.append(abSub.getStackDescription());
        }
		
		return sb.toString();
	}
	
	public static boolean drawCanPlayAI(final AbilityFactory af, SpellAbility sa){
		HashMap<String,String> params = af.getMapParams();
		// AI cannot use this properly until he can use SAs during Humans turn
		if (!ComputerUtil.canPayCost(sa))
			return false;
		
		Target tgt = af.getAbTgt();
		Card source = sa.getSourceCard();
		Cost abCost = af.getAbCost();
		
		if (abCost != null){
			// AI currently disabled for these costs
			if (abCost.getSacCost()){
				return false;
			}
			if (abCost.getLifeCost()){
				if (AllZone.ComputerPlayer.getLife() - abCost.getLifeAmount() < 4)
					return false;
			}
			if (abCost.getDiscardCost()) 	return false;
			
			if (abCost.getSubCounter()) {
				if (abCost.getCounterType().equals(Counters.P1P1)) return false; // Other counters should be used 
			}
			
		}
			
		boolean bFlag = drawTargetAI(af, sa, true, false);
		
		if (!bFlag)
			return false;
		
		if (tgt != null){
			ArrayList<Player> players = tgt.getTargetPlayers();
			if (players.size() > 0 && players.get(0).isHuman())
				return true;
		}
		
		//Don't use draw abilities before main 2 if possible
		if(AllZone.Phase.isBefore(Constant.Phase.Main2) && !params.containsKey("ActivatingPhases"))
        	return false;
		
		//Don't tap creatures that may be able to block
		if(AbilityFactory.waitForBlocking(sa))
			return false;
		
		double chance = .4;	// 40 percent chance of drawing with instant speed stuff
		if (AbilityFactory.isSorcerySpeed(sa))
			chance = .667;	// 66.7% chance for sorcery speed
		if((AllZone.Phase.is(Constant.Phase.End_Of_Turn) && AllZone.Phase.isNextTurn(AllZone.ComputerPlayer)))
			chance = .9;	// 90% for end of opponents turn
		Random r = MyRandom.random;
		boolean randomReturn = r.nextFloat() <= Math.pow(chance, source.getAbilityUsed()+1);
		
		if (AbilityFactory.playReusable(sa))
			randomReturn = true;
		
        Ability_Sub subAb = sa.getSubAbility();
        if (subAb != null)
        	randomReturn &= subAb.chkAI_Drawback();
		return randomReturn;
	}
	
    public static boolean drawTargetAI(AbilityFactory af, SpellAbility sa, boolean primarySA, boolean mandatory) {
        Target tgt = af.getAbTgt();
        HashMap<String,String> params = af.getMapParams();
        Card source = sa.getSourceCard();
        
        int computerHandSize = AllZoneUtil.getCardsInZone(Constant.Zone.Hand, AllZone.ComputerPlayer).size();
        int humanLibrarySize = AllZoneUtil.getCardsInZone(Constant.Zone.Library, AllZone.HumanPlayer).size();
        int computerLibrarySize = AllZoneUtil.getCardsInZone(Constant.Zone.Library, AllZone.ComputerPlayer).size();
        int computerMaxHandSize = AllZone.ComputerPlayer.getMaxHandSize();
        
        int numCards = 1;
        if (params.containsKey("NumCards"))
        	numCards = AbilityFactory.calculateAmount(sa.getSourceCard(), params.get("NumCards"), sa);
        
        boolean xPaid = false;
		String num = af.getMapParams().get("NumCards");
		if (num != null && num.equals("X") && source.getSVar(num).equals("Count$xPaid")){
			// Set PayX here to maximum value.
			if (sa instanceof Ability_Sub)
				numCards = Integer.parseInt(source.getSVar("PayX"));
			else{
				numCards = ComputerUtil.determineLeftoverMana(sa);
				source.setSVar("PayX", Integer.toString(numCards));
			}
			xPaid = true;
		}
        
		// TODO: if xPaid and one of the below reasons would fail, instead of bailing
		// reduce toPay amount to acceptable level
		
        if (tgt != null) {
            // ability is targeted
            tgt.resetTargets();
            
            boolean canTgtHuman = AllZone.HumanPlayer.canTarget(source);
            boolean canTgtComp = AllZone.ComputerPlayer.canTarget(source);
            boolean tgtHuman = false;
            
            if (!canTgtHuman && !canTgtComp)
            	return false;
            
            if (canTgtHuman && !AllZone.HumanPlayer.cantLose() && numCards >= humanLibrarySize) {
                // Deck the Human? DO IT!
                tgt.addTarget(AllZone.HumanPlayer);
                return true;
            }
            
            if (numCards >= computerLibrarySize) {
            	if (xPaid){
    				numCards = computerLibrarySize-1;
    				source.setSVar("PayX", Integer.toString(numCards));
            	}
            	else{
	                // Don't deck your self
	            	if (!mandatory)
	            		return false;
	            	tgtHuman = true;
            	}
            }
            
            if (computerHandSize + numCards > computerMaxHandSize && AllZone.Phase.getPlayerTurn().isComputer()) {
            	if (xPaid){
    				numCards = computerMaxHandSize - computerHandSize;
    				source.setSVar("PayX", Integer.toString(numCards));
            	}
            	else{
	                // Don't draw too many cards and then risk discarding cards at EOT
	            	if (!(af.getMapParams().containsKey("NextUpkeep") || sa instanceof Ability_Sub) && !mandatory)
	            		return false;
            	}
            }
            
            if (numCards == 0)
            	return false;
            
            if ((!tgtHuman || !canTgtHuman) && canTgtComp)
            	tgt.addTarget(AllZone.ComputerPlayer);
            else
            	tgt.addTarget(AllZone.HumanPlayer);
        }
        else {
        	// TODO: consider if human is the defined player
        	
            // ability is not targeted
            if (numCards >= computerLibrarySize) {
                // Don't deck yourself
            	if (!mandatory)
            		return false;
            }
            
            if (computerHandSize + numCards > computerMaxHandSize && AllZone.Phase.getPlayerTurn().isComputer()) {
                // Don't draw too many cards and then risk discarding cards at EOT
            	if (!(af.getMapParams().containsKey("NextUpkeep") || sa instanceof Ability_Sub) && !mandatory)
            		return false;
            }
        }
        return true;
    }// drawTargetAI()
	
    public static boolean drawTrigger(AbilityFactory af, SpellAbility sa, boolean mandatory){
		if (!ComputerUtil.canPayCost(sa))	// If there is a cost payment
			return false;
    	
		if (!drawTargetAI(af, sa, false, mandatory))
			return false;
		
		// check SubAbilities DoTrigger?
		Ability_Sub abSub = sa.getSubAbility();
		if (abSub != null) {
			return abSub.doTrigger(mandatory);
		}
    	
    	return true;
    }
    
	public static void drawResolve(final AbilityFactory af, final SpellAbility sa){
		HashMap<String,String> params = af.getMapParams();
		if (!AbilityFactory.checkConditional(params, sa)){
			AbilityFactory.resolveSubAbility(sa);
			return;
		}
		
		Card source = sa.getSourceCard();
        int numCards = 1;
        if (params.containsKey("NumCards"))
        	numCards = AbilityFactory.calculateAmount(sa.getSourceCard(), params.get("NumCards"), sa);
		
		ArrayList<Player> tgtPlayers;

		Target tgt = af.getAbTgt();
		if (tgt != null)
			tgtPlayers = tgt.getTargetPlayers();
		else
			tgtPlayers = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), params.get("Defined"), sa);
		
		boolean optional = params.containsKey("OptionalDecider");
		boolean slowDraw = params.containsKey("NextUpkeep");
		
		for(Player p : tgtPlayers)
			if (tgt == null || p.canTarget(af.getHostCard())){
				if (optional){
					if (p.isComputer()){
			            if (numCards >= AllZoneUtil.getPlayerCardsInLibrary(p).size()) {
			                // AI shouldn't itself
			                continue;
			            }
					}
					else{
						StringBuilder sb = new StringBuilder();
						sb.append("Do you want to draw ").append(numCards).append(" cards(s)");
						
						if(slowDraw)
							sb.append(" next upkeep");
							
						sb.append("?");
						
						if(!GameActionUtil.showYesNoDialog(sa.getSourceCard(), sb.toString())) 
							continue;
					}
				}

				if (slowDraw)
					for(int i = 0; i < numCards; i++)
						p.addSlowtripList(source);
				else
					p.drawCards(numCards);		
				
			}

		if (af.hasSubAbility()){
			Ability_Sub abSub = sa.getSubAbility();
			if (abSub != null){
	     	   abSub.resolve();
	        }
	        else{
				String DrawBack = params.get("SubAbility");
				if (af.hasSubAbility())
					 CardFactoryUtil.doDrawBack(DrawBack, 0, source.getController(), source.getController().getOpponent(), tgtPlayers.get(0), source, null, sa);
	        }
		}
	}
	
	//**********************************************************************
	//******************************* MILL *********************************
	//**********************************************************************
	
	public static SpellAbility createAbilityMill(final AbilityFactory AF){
		final SpellAbility abMill = new Ability_Activated(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()){
			private static final long serialVersionUID = 5445572699000471299L;
			
			final AbilityFactory af = AF;
			
			@Override
			public String getStackDescription(){
				return millStackDescription(this, af);
			}

			public boolean canPlayAI(){
				return millCanPlayAI(af,this);
			}
			
			@Override
			public void resolve() {
				millResolve(af, this);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return millCanPlayAI(af,this);
			}
			
		};
		return abMill;
	}
	
	public static SpellAbility createSpellMill(final AbilityFactory AF){
		final SpellAbility spMill = new Spell(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()){
			private static final long serialVersionUID = -4990932993654533449L;
			
			final AbilityFactory af = AF;
			
			@Override
			public String getStackDescription(){
				return millStackDescription(this, af);
			}
			
			public boolean canPlayAI(){
				return millCanPlayAI(af, this);
			}
			
			@Override
			public void resolve() {
				millResolve(af, this);
			}
			
		};
		return spMill;
	}
	
	public static SpellAbility createDrawbackMill(final AbilityFactory AF){
		final SpellAbility dbMill = new Ability_Sub(AF.getHostCard(), AF.getAbTgt()){
			private static final long serialVersionUID = -4990932993654533449L;
			
			final AbilityFactory af = AF;
			
			@Override
			public String getStackDescription(){
				return millStackDescription(this, af);
			}

			@Override
			public void resolve() {
				millResolve(af, this);
			}

			@Override
			public boolean chkAI_Drawback() {
				return millDrawback(af, this);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return millTargetAI(af, this, false);
			}
			
		};
		return dbMill;
	}
	
	public static String millStackDescription(SpellAbility sa, AbilityFactory af){
		// when getStackDesc is called, just build exactly what is happening
		StringBuilder sb = new StringBuilder();
		int numCards = AbilityFactory.calculateAmount(sa.getSourceCard(), af.getMapParams().get("NumCards"), sa);
		
		ArrayList<Player> tgtPlayers;

		Target tgt = af.getAbTgt();
		if (tgt != null)
			tgtPlayers = tgt.getTargetPlayers();
		else
			tgtPlayers = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), af.getMapParams().get("Defined"), sa);
		
		if (!(sa instanceof Ability_Sub))
			sb.append(sa.getSourceCard().getName()).append(" - ");
		else
			sb.append(" ");
		
		String conditionDesc = af.getMapParams().get("ConditionDescription");
		if (conditionDesc != null)
			sb.append(conditionDesc).append(" ");
		
		for(Player p : tgtPlayers)
			sb.append(p.toString()).append(" ");
		
		String destination = af.getMapParams().get("Destination");
		if (destination == null || destination.equals(Constant.Zone.Graveyard))
			sb.append("mills ");
		else if (destination.equals(Constant.Zone.Exile))
			sb.append("exiles ");
		sb.append(numCards);
		sb.append(" card");
		if(numCards != 1) sb.append("s");
		sb.append(" from his or her library.");
		
		Ability_Sub abSub = sa.getSubAbility();
        if (abSub != null){
        	sb.append(abSub.getStackDescription());
        }
		
		return sb.toString();
	}
	
	public static boolean millCanPlayAI(final AbilityFactory af, SpellAbility sa){
		HashMap<String,String> params = af.getMapParams();
		if (!ComputerUtil.canPayCost(sa))
			return false;

		Card source = sa.getSourceCard();
		Cost abCost = af.getAbCost();
		
		if (abCost != null){
			// AI currently disabled for these costs
			if (abCost.getSacCost()){
				return false;
			}
			if (abCost.getLifeCost()){
				if (AllZone.ComputerPlayer.getLife() - abCost.getLifeAmount() < 4)
					return false;
			}
			if (abCost.getDiscardCost()) 	return false;
			
			if (abCost.getSubCounter()) {
				if (abCost.getCounterType().equals(Counters.P1P1)) return false; // Other counters should be used 
			}
			
		}
		
		if (!millTargetAI(af, sa, false))
			return false;
		
		Random r = MyRandom.random;
		
		//Don't use draw abilities before main 2 if possible
		if(AllZone.Phase.isBefore(Constant.Phase.Main2) && !params.containsKey("ActivatingPhases"))
        	return false;
		
		//Don't tap creatures that may be able to block
		if(AbilityFactory.waitForBlocking(sa))
			return false;
		
		double chance = .4;	// 40 percent chance of milling with instant speed stuff
		if (AbilityFactory.isSorcerySpeed(sa))
			chance = .667;	// 66.7% chance for sorcery speed
		
		if((AllZone.Phase.is(Constant.Phase.End_Of_Turn) && AllZone.Phase.isNextTurn(AllZone.ComputerPlayer)))
			chance = .9;	// 90% for end of opponents turn
		
		boolean randomReturn = r.nextFloat() <= Math.pow(chance, source.getAbilityUsed()+1);
		
		if (AbilityFactory.playReusable(sa))
			randomReturn = true;
		// some other variables here, like deck size, and phase and other fun stuff

		if (af.getMapParams().get("NumCards").equals("X") && source.getSVar("X").equals("Count$xPaid")){
			// Set PayX here to maximum value.
			int cardsToDiscard = Math.min(ComputerUtil.determineLeftoverMana(sa), 
					AllZoneUtil.getCardsInZone(Constant.Zone.Library, AllZone.HumanPlayer).size());
			source.setSVar("PayX", Integer.toString(cardsToDiscard));
		}
		
		return randomReturn;
	}
	
	public static boolean millTargetAI(AbilityFactory af, SpellAbility sa, boolean mandatory){
		Target tgt = af.getAbTgt();
		HashMap<String,String> params = af.getMapParams();
		
		if (tgt != null){
			tgt.resetTargets();
			Card source = sa.getSourceCard();
			if (!AllZone.HumanPlayer.canTarget(source)){
				if (mandatory && AllZone.ComputerPlayer.canTarget(source)){
					tgt.addTarget(AllZone.ComputerPlayer);
					return true;
				}
				return false;
			}
			
			int numCards = AbilityFactory.calculateAmount(sa.getSourceCard(), params.get("NumCards"), sa);
			
			CardList pLibrary = AllZoneUtil.getCardsInZone(Constant.Zone.Library, AllZone.HumanPlayer);
			
			if (pLibrary.size() == 0){	// deck already empty, no need to mill
				if (!mandatory)
					return false;
				
				tgt.addTarget(AllZone.HumanPlayer);
				return true;
			}
			
			if (numCards >= pLibrary.size()){
				// Can Mill out Human's deck? Do it!
				tgt.addTarget(AllZone.HumanPlayer);
				return true;
			}
			
			// Obscure case when you know what your top card is so you might? want to mill yourself here
			// if (AI wants to mill self)
			// tgt.addTarget(AllZone.ComputerPlayer);
			// else
			tgt.addTarget(AllZone.HumanPlayer);
		}
		return true;
	}
	
    public static boolean millDrawback(AbilityFactory af, SpellAbility sa){
		if (!millTargetAI(af, sa, true))
			return false;
		
		// check SubAbilities DoTrigger?
		Ability_Sub abSub = sa.getSubAbility();
		if (abSub != null) {
			return abSub.chkAI_Drawback();
		}
    	
    	return true;
    }
	
    public static boolean millTrigger(AbilityFactory af, SpellAbility sa, boolean mandatory){
		if (!ComputerUtil.canPayCost(sa))	// If there is a cost payment
			return false;
    	
		if (!millTargetAI(af, sa, mandatory))
			return false;
		
		Card source = sa.getSourceCard();
		if (af.getMapParams().get("NumCards").equals("X") && source.getSVar("X").equals("Count$xPaid")){
			// Set PayX here to maximum value.
			int cardsToDiscard = Math.min(ComputerUtil.determineLeftoverMana(sa), 
					AllZoneUtil.getCardsInZone(Constant.Zone.Library, AllZone.HumanPlayer).size());
			source.setSVar("PayX", Integer.toString(cardsToDiscard));
		}
		
		// check SubAbilities DoTrigger?
		Ability_Sub abSub = sa.getSubAbility();
		if (abSub != null) {
			return abSub.doTrigger(mandatory);
		}
    	
    	return true;
    }
	
	public static void millResolve(final AbilityFactory af, final SpellAbility sa){
		HashMap<String,String> params = af.getMapParams();
		
		if (!AbilityFactory.checkConditional(params, sa)){
			AbilityFactory.resolveSubAbility(sa);
			return;
		}
		
		Card source = sa.getSourceCard();

		int numCards = AbilityFactory.calculateAmount(sa.getSourceCard(), params.get("NumCards"), sa);
		
		ArrayList<Player> tgtPlayers;

		Target tgt = af.getAbTgt();
		if (tgt != null)
			tgtPlayers = tgt.getTargetPlayers();
		else
			tgtPlayers = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), params.get("Defined"), sa);
		
		String destination = params.get("Destination");
		if (destination == null)
			destination = Constant.Zone.Graveyard;
		
		for(Player p : tgtPlayers)
			if (tgt == null || p.canTarget(af.getHostCard()))
				p.mill(numCards, destination);	

		if (af.hasSubAbility()){
			Ability_Sub abSub = sa.getSubAbility();
			if (abSub != null){
	     	   abSub.resolve();
	        }
			else{
				String DrawBack = params.get("SubAbility");
				if (af.hasSubAbility())
					 CardFactoryUtil.doDrawBack(DrawBack, 0, source.getController(), source.getController().getOpponent(), tgtPlayers.get(0), source, null, sa);
			}
		}
	}
	
	//////////////////////
	//
	//Discard stuff
	//
	//////////////////////
	
	//NumCards - the number of cards to be discarded (may be integer or X)
	//Mode	- the mode of discard - should match spDiscard
	//				-Random
	//				-TgtChoose
	//				-RevealYouChoose
	//				-RevealOppChoose
	//				-RevealDiscardAll (defaults to Card if DiscardValid is missing)
	//				-Hand
	//DiscardValid - a ValidCards syntax for acceptable cards to discard
	//UnlessType - a ValidCards expression for "discard x unless you discard a ..."
	
	//Examples:
	//A:SP$Discard | Cost$B | Tgt$TgtP | NumCards$2 | Mode$Random | SpellDescription$<...>
	//A:AB$Discard | Cost$U | ValidTgts$ Opponent | Mode$RevealYouChoose | NumCards$X | SpellDescription$<...>
	
	public static SpellAbility createAbilityDiscard(final AbilityFactory AF) {
		final SpellAbility abDiscard = new Ability_Activated(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()) {
			private static final long serialVersionUID = 4348585353456736817L;
			final AbilityFactory af = AF;
			
			@Override
			public String getStackDescription() {
				// when getStackDesc is called, just build exactly what is happening
				return discardStackDescription(af, this);
			}
			
			@Override
			public boolean canPlayAI() {
				return discardCanPlayAI(af, this);
			}
			
			@Override
			public void resolve() {
				discardResolve(af, this);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return discardTrigger(af, this, mandatory);
			}
			
		};
		return abDiscard;
	}

	public static SpellAbility createSpellDiscard(final AbilityFactory AF) {
		final SpellAbility spDiscard = new Spell(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()) {
			private static final long serialVersionUID = 4348585353456736817L;
			final AbilityFactory af = AF;
			
			@Override
			public String getStackDescription() {
				// when getStackDesc is called, just build exactly what is happening
				return discardStackDescription(af, this);
			}

			@Override
			public boolean canPlayAI() {
				return discardCanPlayAI(af, this);
			}
			
			@Override
			public void resolve() {
				discardResolve(af, this);
			}
			
		};
		return spDiscard;
	}
	
	public static SpellAbility createDrawbackDiscard(final AbilityFactory AF) {
		final SpellAbility dbDiscard = new Ability_Sub(AF.getHostCard(), AF.getAbTgt()) {
			private static final long serialVersionUID = 4348585353456736817L;
			final AbilityFactory af = AF;
			
			@Override
			public String getStackDescription() {
				// when getStackDesc is called, just build exactly what is happening
				return discardStackDescription(af, this);
			}
			
			@Override
			public void resolve() {
				discardResolve(af, this);
			}

			@Override
			public boolean chkAI_Drawback() {
				return discardCheckDrawbackAI(af, this);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return discardTrigger(af, this, mandatory);
			}
			
		};
		return dbDiscard;
	}

	private static void discardResolve(final AbilityFactory af, final SpellAbility sa){
		Card source = sa.getSourceCard();
		HashMap<String,String> params = af.getMapParams();
		
		if (!AbilityFactory.checkConditional(params, sa)){
			AbilityFactory.resolveSubAbility(sa);
			return;
		}

		String mode = params.get("Mode");

		ArrayList<Player> tgtPlayers;

		Target tgt = af.getAbTgt();
		if (tgt != null)
			tgtPlayers = tgt.getTargetPlayers();
		else
			tgtPlayers = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), af.getMapParams().get("Defined"), sa);

		for(Player p : tgtPlayers)
			if (tgt == null || p.canTarget(af.getHostCard())) {	
				if(mode.equals("Hand")) {
					p.discardHand(sa);
					continue;
				}
				
		        int numCards = 1;
		        if (params.containsKey("NumCards"))
		        	numCards = AbilityFactory.calculateAmount(sa.getSourceCard(), params.get("NumCards"), sa);
				
				if(mode.equals("Random")) {
					p.discardRandom(numCards, sa);
				}
				
				else if(mode.equals("TgtChoose")) {
					if(params.containsKey("UnlessType")) {
						p.discardUnless(numCards, params.get("UnlessType"), sa);
					}
					else p.discard(numCards, sa, true);
				}
				
				else if (mode.equals("RevealDiscardAll")){
					// Reveal 
					CardList dPHand = AllZoneUtil.getPlayerHand(p);
					
					if (p.isHuman()){
						// "reveal to computer" for information gathering
					}
					else{
						GuiUtils.getChoiceOptional("Revealed computer hand", dPHand.toArray());
					}
					
					String valid = params.get("DiscardValid");
					if (valid == null)
						valid = "Card";
					
					if (valid.contains("X"))	
						valid = valid.replace("X", Integer.toString(AbilityFactory.calculateAmount(source, "X", sa)));
					
					CardList dPChHand = dPHand.getValidCards(valid.split(","), source.getController(), source);
					
					// Reveal cards that will be discarded?
					for(Card c : dPChHand){
						p.discard(c, sa);
					}
				}

				else if(mode.equals("RevealYouChoose") || mode.equals("RevealOppChoose")) {
					// Is Reveal you choose right? I think the wrong player is being used?
					CardList dPHand = AllZoneUtil.getPlayerHand(p);
					if(dPHand.size() != 0) {
						CardList dPChHand = new CardList(dPHand.toArray());

						if (params.containsKey("DiscardValid")) {	// Restrict card choices
							String[] dValid = params.get("DiscardValid").split(",");
							dPChHand = dPHand.getValidCards(dValid,source.getController(),source);
						}
						Player chooser = null;
						if (mode.equals("RevealYouChoose"))
							chooser = source.getController();
						else chooser = source.getController().getOpponent();
						

						if(chooser.isComputer()){
							//AI
							for(int i = 0; i < numCards; i++) {
								if (dPChHand.size() > 0){
									CardList dChoices = new CardList();
									if(params.containsKey("DiscardValid")) {
										String dValid = params.get("DiscardValid");
										if (dValid.contains("Creature") && !dValid.contains("nonCreature")) {
											Card c = CardFactoryUtil.AI_getBestCreature(dPChHand);
											if (c!=null)
												dChoices.add(CardFactoryUtil.AI_getBestCreature(dPChHand));
										}
									}


									CardListUtil.sortByTextLen(dPChHand);
									dChoices.add(dPChHand.get(0));

									CardListUtil.sortCMC(dPChHand);
									dChoices.add(dPChHand.get(0));

									Card dC = dChoices.get(CardUtil.getRandomIndex(dChoices));
									dPChHand.remove(dC);

									CardList dCs = new CardList();
									dCs.add(dC);
									GuiUtils.getChoiceOptional("Computer has chosen", dCs.toArray());

									AllZone.ComputerPlayer.discard(dC, sa); // is this right?
								}
							}
						}
						else {
							//human
							GuiUtils.getChoiceOptional("Revealed computer hand", dPHand.toArray());

							for(int i = 0; i < numCards; i++) {
								if (dPChHand.size() > 0) {
									Card dC = GuiUtils.getChoice("Choose a card to be discarded", dPChHand.toArray());

									dPChHand.remove(dC);
									AllZone.HumanPlayer.discard(dC, sa); // is this right?
								}
							}
						}
					}
				}
			}

		if (af.hasSubAbility()){
			Ability_Sub abSub = sa.getSubAbility();
			if (abSub != null){
				abSub.resolve();
			}
			else{
				String DrawBack = params.get("SubAbility");
				if (af.hasSubAbility())
					CardFactoryUtil.doDrawBack(DrawBack, 0, source.getController(), source.getController().getOpponent(), source.getController(), source, null, sa);
			}
		}
	}
	
	private static String discardStackDescription(AbilityFactory af, SpellAbility sa){
		HashMap<String,String> params = af.getMapParams();
		String mode = params.get("Mode");
		StringBuilder sb = new StringBuilder();
		
		ArrayList<Player> tgtPlayers;

		Target tgt = af.getAbTgt();
		if (tgt != null)
			tgtPlayers = tgt.getTargetPlayers();
		else
			tgtPlayers = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), params.get("Defined"), sa);
		
		if (!(sa instanceof Ability_Sub))
			sb.append(sa.getSourceCard().getName()).append(" - ");
		else
			sb.append(" ");
		
		String conditionDesc = af.getMapParams().get("ConditionDescription");
		if (conditionDesc != null)
			sb.append(conditionDesc).append(" ");
		
		if (tgtPlayers.size() > 0){
			
			for(Player p : tgtPlayers)
				sb.append(p.toString()).append(" ");
			
			if(mode.equals("RevealYouChoose")) 
				sb.append("reveals his or her hand.").append("  You choose (");
			else if(mode.equals("RevealDiscardAll"))
				sb.append("reveals his or her hand. Discard (");
			else 
				sb.append("discards (");
			
	        int numCards = 1;
	        if (params.containsKey("NumCards"))
	        	numCards = AbilityFactory.calculateAmount(sa.getSourceCard(), params.get("NumCards"), sa);
			
			if(mode.equals("Hand"))
				sb.append("his or her hand");
			else if(mode.equals("RevealDiscardAll"))
				sb.append("All");
			else 
				sb.append(numCards);
				
			sb.append(")");
			
			if(mode.equals("RevealYouChoose")) 
				sb.append(" to discard");
			else if(mode.equals("RevealDiscardAll")){
				String valid = params.get("DiscardValid");
				if (valid == null)
					valid = "Card";
				sb.append(" of type: ").append(valid);
			}
			
			if(mode.equals("Random"))
				sb.append(" at random.");
			else 
				sb.append(".");
		}
		
		Ability_Sub abSub = sa.getSubAbility();
        if (abSub != null)
        	sb.append(abSub.getStackDescription());
		
		return sb.toString();
	}
	
	private static boolean discardCanPlayAI(final AbilityFactory af, SpellAbility sa){
		HashMap<String,String> params = af.getMapParams();
		// AI cannot use this properly until he can use SAs during Humans turn
		if (!ComputerUtil.canPayCost(sa))
			return false;
		
		Target tgt = af.getAbTgt();
		Card source = sa.getSourceCard();
		Cost abCost = af.getAbCost();
		
		if (abCost != null){
			// AI currently disabled for these costs
			if (abCost.getSacCost()){
				return false;
			}
			if (abCost.getLifeCost()){
				if (AllZone.ComputerPlayer.getLife() - abCost.getLifeAmount() < 4)
					return false;
			}
			if (abCost.getDiscardCost()) 	return false;
			
			if (abCost.getSubCounter()) {
				if (abCost.getCounterType().equals(Counters.P1P1)) return false; // Other counters should be used 
			}
			
		}

		boolean humanHasHand = AllZoneUtil.getCardsInZone(Constant.Zone.Hand, AllZone.HumanPlayer).size() > 0;
		
		if (tgt != null){
			if (!humanHasHand)
				return false;
			discardTargetAI(af);
		}
		else{
			// TODO: Add appropriate restrictions
			ArrayList<Player> players = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), af.getMapParams().get("Defined"), sa);
			
			if (players.size() == 1){
				if (players.get(0).isComputer()){
					// the ai should only be using something like this if he has few cards in hand, 
					// cards like this better have a good drawback to be in the AIs deck
				}
				else{
					// defined to the human, so that's fine as long the human has cards
					if (!humanHasHand)
						return false;
				}
			}
			else{
				// Both players discard, any restrictions?
			}
		}
		
		if(!af.getMapParams().get("Mode").equals("Hand")) {
			if (af.getMapParams().get("NumCards").equals("X") && source.getSVar("X").equals("Count$xPaid")){
				// Set PayX here to maximum value.
				int cardsToDiscard = Math.min(ComputerUtil.determineLeftoverMana(sa), 
						AllZoneUtil.getCardsInZone(Constant.Zone.Hand, AllZone.HumanPlayer).size());
				source.setSVar("PayX", Integer.toString(cardsToDiscard));
			}
		}
		
		//Don't use draw abilities before main 2 if possible
		if(AllZone.Phase.isBefore(Constant.Phase.Main2) && !params.containsKey("ActivatingPhases"))
        	return false;
		
		//Don't tap creatures that may be able to block
		if(AbilityFactory.waitForBlocking(sa))
			return false;
		
		double chance = .5;	// 50 percent chance of discarding with instant speed stuff
		if (AbilityFactory.isSorcerySpeed(sa))
			chance = .75;	// 75% chance for sorcery speed
		
		if((AllZone.Phase.is(Constant.Phase.End_Of_Turn) && AllZone.Phase.isNextTurn(AllZone.ComputerPlayer)))
			chance = .9;	// 90% for end of opponents turn
		
		Random r = MyRandom.random;
		boolean randomReturn = r.nextFloat() <= Math.pow(chance, source.getAbilityUsed()+1);
				
		if (AbilityFactory.playReusable(sa))
			randomReturn = true;
		
		// some other variables here, like handsize vs. maxHandSize

        Ability_Sub subAb = sa.getSubAbility();
        if (subAb != null)
        	randomReturn &= subAb.chkAI_Drawback();
		return randomReturn;
	}
	
	private static boolean discardTargetAI(AbilityFactory af) {
		Target tgt = af.getAbTgt();
		if(tgt!= null) {
			if (AllZone.HumanPlayer.canTarget(af.getHostCard())){
				tgt.addTarget(AllZone.HumanPlayer);
				return true;
			}
		}
		return false;
	}// discardTargetAI()
	
	
	private static boolean discardTrigger(AbilityFactory af, SpellAbility sa, boolean mandatory) {
		if (!ComputerUtil.canPayCost(sa))
			return false;
		
		Target tgt = af.getAbTgt();
		if(tgt!= null) {
			if (!discardTargetAI(af)){
				if (mandatory && AllZone.ComputerPlayer.canTarget(af.getHostCard()))
					tgt.addTarget(AllZone.ComputerPlayer);
				else
					return false;
			}
		}


		return true;
	}// discardCheckDrawbackAI()
	
	private static boolean discardCheckDrawbackAI(AbilityFactory af, Ability_Sub subAb) {
		// Drawback AI improvements
		// if parent draws cards, make sure cards in hand + cards drawn > 0
		Target tgt = af.getAbTgt();
		if(tgt!= null) {
			discardTargetAI(af);
		}
		// TODO: check for some extra things
		return true;
	}// discardCheckDrawbackAI()
	
	//**********************************************************************
	//******************************* Shuffle ******************************
	//**********************************************************************

	public static SpellAbility createAbilityShuffle(final AbilityFactory af) {
		final SpellAbility abShuffle = new Ability_Activated(af.getHostCard(), af.getAbCost(), af.getAbTgt()) {
			private static final long serialVersionUID = -1245185178904838198L;

			@Override
			public String getStackDescription() {
				return shuffleStackDescription(af, this);
			}

			@Override
			public boolean canPlayAI() {
				return shuffleCanPlayAI(af, this);
			}

			@Override
			public void resolve() {
				shuffleResolve(af, this);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return shuffleTrigger(af, this, mandatory);
			}

		};
		return abShuffle;
	}

	public static SpellAbility createSpellShuffle(final AbilityFactory af) {
		final SpellAbility spShuffle = new Spell(af.getHostCard(), af.getAbCost(), af.getAbTgt()) {
			private static final long serialVersionUID = 589035800601547559L;

			@Override
			public String getStackDescription() {
				return shuffleStackDescription(af, this);
			}

			@Override
			public boolean canPlayAI() {
				return shuffleCanPlayAI(af, this);
			}

			@Override
			public void resolve() {
				shuffleResolve(af, this);
			}

		};
		return spShuffle;
	}

	public static SpellAbility createDrawbackShuffle(final AbilityFactory af) {
		final SpellAbility dbShuffle = new Ability_Sub(af.getHostCard(), af.getAbTgt()) {
			private static final long serialVersionUID = 5974307947494280639L;

			@Override
			public String getStackDescription() {
				// when getStackDesc is called, just build exactly what is happening
				return shuffleStackDescription(af, this);
			}

			@Override
			public void resolve() {
				shuffleResolve(af, this);
			}

			@Override
			public boolean chkAI_Drawback() {
				return shuffleTargetAI(af, this, false, false);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return shuffleTrigger(af, this, mandatory);
			}

		};
		return dbShuffle;
	}

	private static String shuffleStackDescription(AbilityFactory af, SpellAbility sa) {
		StringBuilder sb = new StringBuilder();

		if (!(sa instanceof Ability_Sub))
			sb.append(sa.getSourceCard().getName()).append(" - ");
		else
			sb.append(" ");
		
		String conditionDesc = af.getMapParams().get("ConditionDescription");
		if (conditionDesc != null)
			sb.append(conditionDesc).append(" ");

		ArrayList<Player> tgtPlayers;

		Target tgt = af.getAbTgt();
		if (tgt != null)
			tgtPlayers = tgt.getTargetPlayers();
		else
			tgtPlayers = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), af.getMapParams().get("Defined"), sa);

		if (tgtPlayers.size() > 0){
			Iterator<Player> it = tgtPlayers.iterator();
			while(it.hasNext()) {
				sb.append(it.next().getName());
				if(it.hasNext()) sb.append(" and ");
			}
		}
		else {
			sb.append("Error - no target players for RevealHand. ");
		}
		sb.append(" shuffle");
		if(tgtPlayers.size() > 1) sb.append(" their libraries");
		else sb.append("s his or her library");
		sb.append(".");

		Ability_Sub abSub = sa.getSubAbility();
		if (abSub != null){
			sb.append(abSub.getStackDescription());
		}

		return sb.toString();
	}

	private static boolean shuffleCanPlayAI(final AbilityFactory af, SpellAbility sa) {
		//not really sure when the compy would use this; maybe only after a human
		// deliberately put a card on top of their library
		return false;
		/*
		if (!ComputerUtil.canPayCost(sa))
			return false;

		Card source = sa.getSourceCard();

		Random r = MyRandom.random;
		boolean randomReturn = r.nextFloat() <= Math.pow(.667, source.getAbilityUsed()+1);

		if (AbilityFactory.playReusable(sa))
			randomReturn = true;

		Ability_Sub subAb = sa.getSubAbility();
		if (subAb != null)
			randomReturn &= subAb.chkAI_Drawback();
		return randomReturn;
		*/
	}

	private static boolean shuffleTargetAI(AbilityFactory af, SpellAbility sa, boolean primarySA, boolean mandatory) {
		return false;
	}// revealHandTargetAI()

	private static boolean shuffleTrigger(AbilityFactory af, SpellAbility sa, boolean mandatory) {
		if (!ComputerUtil.canPayCost(sa))	// If there is a cost payment
			return false;

		if (!shuffleTargetAI(af, sa, false, mandatory))
			return false;

		// check SubAbilities DoTrigger?
		Ability_Sub abSub = sa.getSubAbility();
		if (abSub != null) {
			return abSub.doTrigger(mandatory);
		}

		return true;
	}

	private static void shuffleResolve(final AbilityFactory af, final SpellAbility sa) {
		Card host = af.getHostCard();
		HashMap<String,String> params = af.getMapParams();
		boolean optional = params.containsKey("Optional");
		
		if (!AbilityFactory.checkConditional(params, sa)){
			AbilityFactory.resolveSubAbility(sa);
			return;
		}

		ArrayList<Player> tgtPlayers;

		Target tgt = af.getAbTgt();
		if (tgt != null)
			tgtPlayers = tgt.getTargetPlayers();
		else
			tgtPlayers = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), params.get("Defined"), sa);

		for(Player p : tgtPlayers) {
			if (tgt == null || p.canTarget(af.getHostCard())) {
				if(optional && sa.getActivatingPlayer().isHuman() && !GameActionUtil.showYesNoDialog(host, "Have "+p+" shuffle?")) {
					; //do nothing
				}
				else {
					p.shuffle();
				}
			}
		}

		if(af.hasSubAbility()) {
			Ability_Sub abSub = sa.getSubAbility();
			if(abSub != null) abSub.resolve();
		}
	}
	
}//end class AbilityFactory_ZoneAffecting
