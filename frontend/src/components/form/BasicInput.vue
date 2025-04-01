<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue';
import Button from 'primevue/button';

// Define props
const props = defineProps<{
  modelValue: string;
  id: string; // Unique identifier for the input
  label?: string; // Optional placeholder
  type?: string; // Type of the input (text, password, etc.)
  disabled?: boolean; // Disabled state of the input
  error?: boolean; // Error message to display
  helperText?: string | null; // Helper text to display
  placeholder?: string; // Placeholder text
}>();

// Define emits for v-model
const emit = defineEmits<{
  (event: 'update:modelValue', value: string): void;
}>();

// Internal ref for the input value
const inputValue = ref(props.modelValue);
const toggleVisibility = ref<boolean>(false);

// Computed property to dynamically change the input type
const inputType = ref(props.type || 'text');

// Watch for input changes and emit the updated value
const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  inputValue.value = target.value;
  emit('update:modelValue', inputValue.value);
};

// Toggle password visibility
const togglePasswordVisibility = () => {
  toggleVisibility.value = !toggleVisibility.value;
  inputType.value = toggleVisibility.value ? 'text' : 'password';
};
</script>

<template>
  <div class="flex flex-col gap-2">
    <label v-if="label" :for="props.id" class="text-md font-bold text-slate-600 w-fit">{{ props.label }}</label>
    <div class="relative">
      <input
        v-bind="$attrs" 
        :id="props.id"
        :value="inputValue"
        :type="inputType"
        :placeholder="props.type === 'email' ? 'example@email.com' : props.placeholder"
        :disabled="props.disabled"
        @input="handleInput"
        :style="props.type === 'password' ? 'padding-right: 28px !important;' : ''"
        :class="['w-full rounded-md focus:outline-main focus:outline-2 border-2 border-slate-400 p-2', props.error ? 'bg-red-100 !focus:outline-red-600 !border-red-400' : '']"
      />
      <Button
        v-if="props.type === 'password'"
        @click="togglePasswordVisibility"
        size="small"
        :icon="toggleVisibility ? 'pi pi-eye' : 'pi pi-eye-slash'"
        class="!absolute top-[13%] right-[8px]"
        variant="text"
        rounded
        aria-label="Toggle password visibility"
      />
    </div>
    <p v-if="props.error" class="text-red-500 text-sm">{{ props.helperText }}</p>
  </div>
</template>

<style scoped>
/* Optional custom styles */
</style>
