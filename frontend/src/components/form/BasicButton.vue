<template>
    <button
      class="flex items-center cursor-pointer justify-center gap-3 px-4 py-2 transition-all hover:outline font-bold text-white rounded-md disabled:opacity-60 disabled:cursor-not-allowed"
      :class="[
        variantClasses[variant],
        { 'hover:bg-opacity-90': !disabled }
      ]"
      :disabled="disabled"
      @click="handleClick"
    >
      <slot></slot>
    </button>
  </template>
  
  <script lang="ts">
  import { defineComponent } from 'vue';
  
  type ButtonVariant = 'success' | 'failure' | 'neutral';
  
  export default defineComponent({
    name: 'Button',
    props: {
      variant: {
        type: String as () => ButtonVariant,
        default: 'neutral',
        validator: (value: string) => {
          return ['success', 'failure', 'neutral'].includes(value);
        }
      },
      disabled: {
        type: Boolean,
        default: false
      }
    },
    emits: ['click'],
    setup(props, { emit }) {
      const variantClasses: Record<ButtonVariant, string> = {
        success: 'bg-green-600 hover:bg-green-700',
        failure: 'bg-red-600 hover:bg-red-700',
        neutral: 'bg-main hover:bg-main-dark'
      };
  
      const handleClick = (event: MouseEvent) => {
        if (!props.disabled) {
          emit('click', event);
        }
      };
  
      return {
        handleClick,
        variantClasses
      };
    }
  });
  </script>