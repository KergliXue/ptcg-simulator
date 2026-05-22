import { SuperType } from './card-types';
import { Effect } from '../effects/effect';
import { State } from '../state/state';
import { StoreLike } from '../store-like';

export interface CardArt {
  rarity: string;
  imageUrl: string;
}

export abstract class Card {

  public abstract set: string;

  public abstract setNumber: string;

  public abstract superType: SuperType;

  public abstract fullName: string;

  public abstract name: string;

  public id: number = -1;

  public tags: string[] = [];

  public arts: CardArt[] = [];

  public reduceEffect(store: StoreLike, state: State, effect: Effect): State {
    return state;
  }

}
