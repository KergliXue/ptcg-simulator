import { Card } from './card';
import { SuperType, CardType, EnergyType } from './card-types';


export abstract class EnergyCard extends Card {

  public superType: SuperType = SuperType.ENERGY;

  public setNumber: string = '';
  
  public energyType: EnergyType = EnergyType.BASIC;

  public provides: CardType[] = [];

  public text: string = '';
}
