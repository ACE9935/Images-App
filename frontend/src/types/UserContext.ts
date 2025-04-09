import type { User } from './UserInterface';
import type { Ref } from 'vue';

export interface UserContext {
    user: Ref<User>; // Define the User type properly
    refreshUser: () => void | null;
    loading: Ref<boolean>;
  }